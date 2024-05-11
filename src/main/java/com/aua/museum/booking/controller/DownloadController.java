package com.aua.museum.booking.controller;

import com.aua.museum.booking.domain.User;
import com.aua.museum.booking.service.DownloadService;
import com.aua.museum.booking.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.Principal;
import java.util.Locale;

@Controller
@RequestMapping("/download-events")
@RequiredArgsConstructor
public class DownloadController {
    private final UserService userService;
    private final DownloadService downloadService;


    @PostMapping("/csv")
    public void downloadCsv(
            @RequestParam(value = "currentDate", required = false) String filename,
            Principal principal, HttpServletResponse response, HttpServletRequest request, Locale locale) throws IOException {
        response.addHeader("Content-Type", "text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename + ".csv");
        User user = userService.getUserByUsername(principal.getName());
        downloadService.downloadCsv(user.getUsername(), response, request, locale);
    }

    @PostMapping("/pdf")
    public void downloadPdf(HttpServletRequest request, Principal principal,
                            HttpServletResponse response, Locale locale) throws IOException {
        response.addHeader("Content-Type", "application/pdf");
        String filename = request.getParameter("currentDate");
        response.addHeader("Content-Disposition", "attachment; filename=" + filename + ".pdf");
        downloadService.downloadPdf(principal.getName(), response, request, locale);
    }
}

