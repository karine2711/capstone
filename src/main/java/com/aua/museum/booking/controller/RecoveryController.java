package com.aua.museum.booking.controller;

import com.aua.museum.booking.cookies.JwtUtil;
import com.aua.museum.booking.cookies.RememberMeCookieService;
import com.aua.museum.booking.cookies.ValidatePasswordResetCookie;
import com.aua.museum.booking.domain.EventState;
import com.aua.museum.booking.domain.Question;
import com.aua.museum.booking.domain.User;
import com.aua.museum.booking.domain.UserState;
import com.aua.museum.booking.dto.QuestionDetailsDto;
import com.aua.museum.booking.exception.notfound.UserNotFoundException;
import com.aua.museum.booking.service.EventService;
import com.aua.museum.booking.service.QuestionDetailsService;
import com.aua.museum.booking.service.QuestionService;
import com.aua.museum.booking.service.UserService;
import com.aua.museum.booking.validation.ValidEmail;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/forgot-password")
@RequiredArgsConstructor
@Validated
@PropertySource("classpath:values.properties")
public class RecoveryController {

    private final QuestionDetailsService questionDetailsService;
    private final QuestionService questionService;
    private final UserService userService;
    private final EventService eventService;
    private final JwtUtil jwtUtil;
    private final ValidatePasswordResetCookie validateResetCookie;
    private final RememberMeCookieService rememberMeCookieService;

    @GetMapping
    public ModelAndView getRecoveryPage(Principal principal) {
        if (principal != null) return new ModelAndView("redirect:/homepage");
        return new ModelAndView(Templates.FORGOT_PASSWORD.getName());
    }

    @PostMapping(value = "/{email}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> checkUser(@PathVariable("email") @ValidEmail String email,
                                            HttpServletRequest request) {
        User user = userService.getUserByEmail(email);

        if (user.isSuperAdmin()) {
            throw new UserNotFoundException(email);
        }

        if (user.getState() == UserState.UNBLOCKED) {
            return new ResponseEntity<>(HttpStatus.SEE_OTHER);
        }

        if (validateResetCookie.validateCookie(request, email)) {
            return new ResponseEntity<>(HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/questions")
    public String getUserQuestionsByEmail(Principal principal) {
        if (principal != null) return "redirect:/homepage";
        return Templates.QUESTIONS.getName();
    }

    @ResponseBody
    @PostMapping(value = "/questions/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Map<String, Object>>> getUserQuestionsByEmail(@PathVariable("email") @ValidEmail String email) {
        final List<Question> questions = questionDetailsService.getQuestionsByEmail(email);
        List<Map<String, Object>> userQuestions = questions.stream()
                .map(questionService::getLocalizedQuestionAsMap).collect(Collectors.toList());
        return new ResponseEntity<>(userQuestions, HttpStatus.OK);
    }

    @PostMapping(value = "/questions/check-answers/{email}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> checkAnswersAndRedirect(@PathVariable("email") @ValidEmail String email,
                                                          @RequestBody QuestionDetailsDto questionDetailsDto,
                                                          HttpServletResponse response) {
        String answer = questionDetailsDto.getAnswer();
        int questionId = questionDetailsDto.getQuestionId();
        if (questionDetailsService.checkAnswers(questionId, answer, email)) {
            String token = jwtUtil.generateToken(email);
            Cookie cookie = new Cookie("reset-password", token);
            cookie.setPath("/" + Templates.FORGOT_PASSWORD.getName());
            cookie.setMaxAge(60 * 60 * 2);
            response.addCookie(cookie);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping(value = "questions/block-user/{email}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> blockUser(@PathVariable("email") @ValidEmail String email) {
        User user = userService.getUserByEmail(email);
        eventService.getAllEvents().stream()
                .filter(e -> e.getUser().equals(user)).forEach(e -> e.setEventState(EventState.PRE_BOOKED));
        userService.blockUserByEmail(email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reset-password/{email}")
    public String getChangePasswordPage(
            @PathVariable @ValidEmail String email,
            Principal principal,
            HttpServletRequest request) {
        if (principal != null || (!validateResetCookie.validateCookie(request, email) && userService.getUserByEmail(email).getState() != UserState.UNBLOCKED))
            return "redirect:/homepage";
        return Templates.RESET_PASSWORD.getName();
    }

    @PostMapping("/reset-password/{email}")
    public ResponseEntity<Object> submitNewPassword(@PathVariable("email") @ValidEmail String email,
                                                    @RequestBody Map<String, String> body,
                                                    HttpServletRequest request, HttpServletResponse response) throws ServletException {
        User user = userService.getUserByEmail(email);
        if (!validateResetCookie.validateCookie(request, email) && user.getState() != UserState.UNBLOCKED) {
            return ResponseEntity.badRequest().build();
        }
        String password = body.get("password");
        boolean rememberMe = Boolean.parseBoolean(body.getOrDefault("rememberMe", "false"));

        eventService.getAllEvents().stream()
                .filter(e -> e.getUser().equals(user)).forEach(e -> e.setEventState(EventState.BOOKED));
        userService.toActive(user.getId());
        userService.changePassword(email, password);
        request.login(user.getUsername(), password);
        if (rememberMe) {
            attachCookieToResponse(user, response);
        }
        return ResponseEntity.ok().build();
    }

    private void attachCookieToResponse(User user, HttpServletResponse response) {
        Cookie rememberMeCookie = rememberMeCookieService.getCookie(user);
        response.addCookie(rememberMeCookie);
    }
}