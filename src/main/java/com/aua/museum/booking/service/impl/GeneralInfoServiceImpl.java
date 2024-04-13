package com.aua.museum.booking.service.impl;



import com.aua.museum.booking.domain.GeneralInfo;
import com.aua.museum.booking.repository.GeneralInfoRepository;
import com.aua.museum.booking.repository.WeekDayRepository;
import com.aua.museum.booking.service.GeneralInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class GeneralInfoServiceImpl implements GeneralInfoService {

    private final GeneralInfoRepository generalInfoRepository;
    private final WeekDayRepository weekDayRepository;

    @Override
    public void updateGeneralInfo(GeneralInfo generalInfo) {
        generalInfo.setId(1L);
        generalInfoRepository.save(generalInfo);
    }

    @Override
    public GeneralInfo getGeneralInfo(Long id) {
        return generalInfoRepository.findById(id).orElse(null);
    }

    @Override
    public List<String> getAllWeekDaysByLocale(Locale locale) {
        switch (locale.getLanguage().toUpperCase()) {
            case "RU":
                return weekDayRepository.getAllWeekDaysInRussian();
            case "EN":
                return weekDayRepository.getAllWeekDaysInEnglish();
            default:
                return weekDayRepository.getAllWeekDaysInArmenian();
        }
    }
}