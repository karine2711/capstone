package com.aua.museum.booking.service;

import com.aua.museum.booking.domain.GeneralInfo;

import java.util.List;
import java.util.Locale;

public interface GeneralInfoService {
    void updateGeneralInfo(GeneralInfo generalInfo);

    GeneralInfo getGeneralInfo(Long id);

    List<String> getAllWeekDaysByLocale(Locale locale);
}
