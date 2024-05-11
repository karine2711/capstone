package com.aua.museum.booking.cookies;

import com.aua.museum.booking.domain.User;
import com.aua.museum.booking.exception.notfound.UserNotFoundException;
import com.aua.museum.booking.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
@RequiredArgsConstructor
public class ValidatePasswordResetCookie {
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public Boolean validateCookie(HttpServletRequest request, String requestedEmail) {
        Cookie resetCookie = WebUtils.getCookie(request, "reset-password");
        if (resetCookie != null) {
            String jwt = resetCookie.getValue();
            User user;
            try {
                user = userService.getUserByEmail(requestedEmail);
            } catch (UserNotFoundException e) {
                return false;
            }
            return jwtUtil.validateToken(jwt, user);
        }
        return false;
    }
}