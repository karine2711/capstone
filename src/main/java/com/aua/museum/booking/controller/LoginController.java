package com.aua.museum.booking.controller;

import com.aua.museum.booking.domain.User;
import com.aua.museum.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @GetMapping("login")
    public String login(HttpServletRequest request, Principal principal) {
        if (principal != null) {
            return "redirect:/homepage";
        }

        Cookie rememberMeCookie = WebUtils.getCookie(request, "remember-me");

        return (rememberMeCookie != null) ? "redirect:/homepage" : Templates.LOGIN.getName();
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            String username = auth.getName();
            User user = userService.getUserByUsername(username);
            if (user.getToken() != null) {
                user.setToken(null);
                userService.save(user);
            }
            Cookie rememberMe = WebUtils.getCookie(request, "remember-me");
            Cookie jsessionid = WebUtils.getCookie(request, "JSESSIONID");
            deleteCookieIfExists(rememberMe, response);
            deleteCookieIfExists(jsessionid,response);
            deleteCookieIfExists(jsessionid, response);
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/";
    }

    private void deleteCookieIfExists(Cookie cookie, HttpServletResponse response) {
        if (cookie != null) {
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }
}
