package com.aua.museum.booking.controller;

import com.aua.museum.booking.domain.EventState;
import com.aua.museum.booking.domain.Role;
import com.aua.museum.booking.domain.RoleEnum;
import com.aua.museum.booking.domain.User;
import com.aua.museum.booking.service.EventService;
import com.aua.museum.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        List userList = new ArrayList();
        userList.add(user);
        modelAndView.setViewName(Templates.USER_LIST.getName());
        modelAndView.addObject("listUsers", userList);
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
        List<User> expectedResult = new ArrayList<>();
        for (User user : users) {
            List<RoleEnum> roles = user.getRoles().stream()
                    .map(Role::getRoleName)
                    .collect(Collectors.toList());
            if (!roles.contains(RoleEnum.SUPER_ADMIN_ROLE)) {
                expectedResult.add(user);
            }
        }
        return expectedResult;
    }
}
