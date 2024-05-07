package com.aua.museum.booking.controller;

import com.aua.museum.booking.domain.GeneralInfo;
import com.aua.museum.booking.domain.Role;
import com.aua.museum.booking.security.UserDetailsServiceImpl;
import com.aua.museum.booking.service.GeneralInfoService;
import com.aua.museum.booking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Locale;

@Controller
public class LandingController {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    GeneralInfoService generalInfoService;

    @Autowired
    UserService userService;


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

    @GetMapping("/calendar/admin")
    public String calendarAdmin() {
        return "calendarAdmin";
    }

    @GetMapping("/homepage")
    public String success(Model model, Principal principal, HttpSession session) {
        model.addAttribute("currentUser", principal.getName());
        UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority(Role.ADMIN_ROLE.name())))
            return Templates.HOMEPAGE_ADMIN.getName();
        return Templates.HOMEPAGE.getName();
    }

    @GetMapping("/myActivities")
    public String myActivities(Model model, Principal principal) {
        model.addAttribute("currentUser", principal.getName());
        UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority(Role.ADMIN_ROLE.name())))
            return Templates.HOMEPAGE_ADMIN.getName();
        return Templates.HOMEPAGE.getName();
    }
}
