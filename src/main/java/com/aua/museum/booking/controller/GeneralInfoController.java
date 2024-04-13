package com.aua.museum.booking.controller;

import com.aua.museum.booking.domain.GeneralInfo;
import com.aua.museum.booking.dto.GeneralInfoDto;
import com.aua.museum.booking.mapping.GeneralInfoMapperDecorator;
import com.aua.museum.booking.service.GeneralInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Locale;

@Controller
@RequestMapping("/homepage")
@RequiredArgsConstructor
public class GeneralInfoController {

    private final GeneralInfoService generalInfoService;
    private final GeneralInfoMapperDecorator generalInfoMapper;

    @PreAuthorize("hasAnyAuthority('ADMIN_ROLE,SUPER_ADMIN_ROLE')")
    @GetMapping("/update-content")
    public ModelAndView getUpdateContentPage(Locale locale) {
        ModelAndView model = new ModelAndView("update-content");
        model.addObject("generalInfo", generalInfoService.getGeneralInfo(1L));
        model.addObject("weekDays", generalInfoService.getAllWeekDaysByLocale(locale));
        return model;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN_ROLE,SUPER_ADMIN_ROLE')")
    @PostMapping("/update-content")
    public @ResponseBody
    ResponseEntity<Object> updateGeneralInfo(@ModelAttribute GeneralInfoDto generalInfoDto) {
        GeneralInfo generalInfo = generalInfoMapper.toEntity(generalInfoDto);
        generalInfoService.updateGeneralInfo(generalInfo);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
