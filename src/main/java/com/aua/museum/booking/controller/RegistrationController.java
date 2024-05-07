package com.aua.museum.booking.controller;

import com.aua.museum.booking.cookies.RememberMeCookieService;
import com.aua.museum.booking.domain.User;
import com.aua.museum.booking.dto.UserDto;
import com.aua.museum.booking.exception.notunique.FieldsAlreadyExistException;
import com.aua.museum.booking.mapping.UserMapperDecorator;
import com.aua.museum.booking.service.QuestionService;
import com.aua.museum.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
@PropertySource("classpath:values.properties")
public class RegistrationController {

    private final UserService userService;
    private final UserMapperDecorator userMapperDecorator;
    private final QuestionService questionService;
    private final RememberMeCookieService rememberMeCookieService;
    private final MessageSource messageSource;
    @Value("${security.secret.key}")
    private String SECRET_KEY;

    @PostMapping
    public ModelAndView registerUser(@ModelAttribute("user") @Validated UserDto userDto,
                                     BindingResult result,
                                     ModelAndView modelAndView,
                                     HttpServletRequest request,
                                     HttpServletResponse response,
                                     Locale locale) {
        if (result.hasErrors()) {
            modelAndView.setViewName(Templates.REGISTRATION.getName());
            modelAndView.addObject("questions", getSecurityQuestions());
            modelAndView.addObject("fieldErrors", result.getFieldErrors());
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndView;
        }
        User user = userMapperDecorator.toEntity(userDto);
        try {
            userService.createUser(user);
        } catch (FieldsAlreadyExistException e) {
            modelAndView.setViewName(com.aua.museum.booking.controller.Templates.REGISTRATION.getName());
            final Map<String, Boolean> fieldsErrors = e.getFieldsErrors();
            String usernameMessage = messageSource.getMessage("valid.userDto.userName.unique.message", new Object[]{}, locale);
            String emailMessage = messageSource.getMessage("valid.userDto.email.unique.message", new Object[]{}, locale);
            modelAndView.addObject("questions", getSecurityQuestions());
            if (fieldsErrors.get("username")) modelAndView.addObject("usernameError", usernameMessage);
            if (fieldsErrors.get("email")) modelAndView.addObject("emailError", emailMessage);
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndView;
        }
        try {
            request.login(userDto.getUsername(), userDto.getPassword());
            attachCookieToResponse(user, response);
        } catch (ServletException e) {
            modelAndView.setViewName("redirect:/login");
            return modelAndView;
        }
        modelAndView.setViewName("redirect:/homepage");
        return modelAndView;
    }

    private void attachCookieToResponse(User user, HttpServletResponse response) {
        Cookie rememberMeCookie = rememberMeCookieService.getCookie(user);
        response.addCookie(rememberMeCookie);
    }


    @GetMapping
    public ModelAndView registrationPage(ModelAndView modelAndView, Principal principal, HttpServletResponse response) {
        if (principal != null) {
            return new ModelAndView("redirect:/homepage");
        }
        modelAndView.setViewName(com.aua.museum.booking.controller.Templates.REGISTRATION.getName());
        modelAndView.addObject("user", new UserDto());
        modelAndView.addObject("questions", getSecurityQuestions());
        modelAndView.setStatus(HttpStatus.OK);
        return modelAndView;
    }

    private Map<Integer, String> getSecurityQuestions() {
        Map<Integer, String> questions = new HashMap<>();
        questionService.getAllQuestions()
                .forEach(question -> questions.put(question.getId(), questionService.getQuestionFromLocale(question)));
        return questions;
    }
}