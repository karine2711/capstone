package com.aua.museum.booking.controller;

import com.aua.museum.booking.domain.GeneralInfo;
import com.aua.museum.booking.security.UserDetailsServiceImpl;
import com.aua.museum.booking.service.GeneralInfoService;
import com.aua.museum.booking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Locale;

@Controller
public class LandingController {


    UserDetailsServiceImpl userDetailsService;
    GeneralInfoService generalInfoService;
    UserService userService;

    @Autowired
    public LandingController(UserDetailsServiceImpl userDetailsService, GeneralInfoService generalInfoService, UserService userService) {
        this.userDetailsService = userDetailsService;
        this.generalInfoService = generalInfoService;
        this.userService = userService;
    }

    @GetMapping("/*")
    public String landing(Principal principal) {
        if (principal != null)
            return "redirect:/homepage";
        return "landing";
    }

    @GetMapping("/title")
    public ResponseEntity<String> getTitle(Locale locale) {
        return ResponseEntity.ok(generalInfoService.getGeneralInfo(1L).getTitleByLocale(locale));
    }

    @GetMapping("/footer-info")
    public ResponseEntity<GeneralInfo> getFooterInfo() {
        return ResponseEntity.ok(generalInfoService.getGeneralInfo(1L));
    }

    @GetMapping("/calendar")
    public String calendar() {
        return "calendar";
    }


    @GetMapping("/homepage")
    public String success(Model model, Principal principal) {
        model.addAttribute("currentUser", principal.getName());
        return Templates.HOMEPAGE.getName();
    }

    @GetMapping("/myActivities")
    public String myActivities(Model model, Principal principal) {
        model.addAttribute("currentUser", principal.getName());
        return Templates.HOMEPAGE.getName();
    }

    @GetMapping("/loginPage")
    public String login() {
        return Templates.LOGIN.getName();
    }
}
