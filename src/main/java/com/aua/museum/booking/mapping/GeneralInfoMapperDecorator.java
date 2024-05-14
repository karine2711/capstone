package com.aua.museum.booking.mapping;

import com.aua.museum.booking.domain.GeneralInfo;
import com.aua.museum.booking.dto.GeneralInfoDto;
import com.aua.museum.booking.repository.WeekDayRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@NoArgsConstructor
public class GeneralInfoMapperDecorator implements GeneralInfoMapper {

    @Autowired
    private GeneralInfoMapper generalInfoMapper;

    @Autowired
    private WeekDayRepository weekDayRepository;

    @Override
    public GeneralInfo toEntity(GeneralInfoDto generalInfoDto) {
        GeneralInfo generalInfo = generalInfoMapper.toEntity(generalInfoDto);
        generalInfo.setStartWorkingDay(weekDayRepository.getReferenceById(generalInfoDto.getStartWorkingDayId()));
        generalInfo.setEndWorkingDay(weekDayRepository.getReferenceById(generalInfoDto.getEndWorkingDayId()));
        return generalInfo;
    }
}
