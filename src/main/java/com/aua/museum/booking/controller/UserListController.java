package com.aua.museum.booking.controller;

import com.aua.museum.booking.domain.EventState;
import com.aua.museum.booking.domain.User;
import com.aua.museum.booking.service.EventService;
import com.aua.museum.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserListController {

    private final UserService userService;
    private final EventService eventService;


    @GetMapping
    public ModelAndView viewPage(ModelAndView modelAndView) {

        List<User> listEmployees = getOnlyUsersAndAdmins(userService.getAllUsers());
        modelAndView.setViewName(Templates.USER_LIST.getName());
        modelAndView.addObject("listUsers", listEmployees);
        return modelAndView;
    }

    @GetMapping("/{username}")
    public ModelAndView findByUserName(ModelAndView modelAndView, @PathVariable String username) {
        User user = userService.getUserByUsername(username);
        modelAndView.setViewName(Templates.USER_LIST.getName());
        modelAndView.addObject("listUsers", List.of(user));
        return modelAndView;
    }

    @GetMapping("/unBlock/{id}")
    public ModelAndView unBlock(@PathVariable Long id, ModelAndView modelAndView) {
        userService.unblockUserById(id);
        return viewPage(modelAndView);
    }

    @GetMapping("/toActive/{id}")
    public ModelAndView toActive(@PathVariable Long id, ModelAndView modelAndView) {
        User user = userService.getUserById(id);
        eventService.getAllEvents().stream()
                .filter(e -> e.getUser().equals(user)).forEach(e -> e.setEventState(EventState.BOOKED));
        userService.toActive(id);
        return viewPage(modelAndView);
    }

    @GetMapping("/toUser/{id}")
    public ModelAndView toUser(@PathVariable Long id, ModelAndView modelAndView) {
        userService.toUser(id);
        return viewPage(modelAndView);
    }

    @GetMapping("/toAdmin/{id}")
    public ModelAndView toAdmin(@PathVariable Long id, ModelAndView modelAndView) {
        userService.toAdmin(id);
        return viewPage(modelAndView);
    }

    private List<User> getOnlyUsersAndAdmins(List<User> users) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return users.parallelStream()
                .filter(u -> !u.isSuperAdmin() && !u.getUsername().equals(authentication.getName()))
                .toList();
    }
}
