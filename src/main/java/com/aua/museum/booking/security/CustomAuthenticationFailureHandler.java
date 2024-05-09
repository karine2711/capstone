package com.aua.museum.booking.security;

import com.aua.museum.booking.exception.UserAccountIsAlreadyBlockedException;
import com.aua.museum.booking.exception.notfound.UserNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        if (e.getCause() instanceof UserNotFoundException) {
            httpServletResponse.setStatus(404);
        } else if (e.getCause() instanceof UserAccountIsAlreadyBlockedException) {
            httpServletResponse.setStatus(409);
        } else if (e instanceof BadCredentialsException) {
            httpServletResponse.setStatus(401);
        }
    }
}
