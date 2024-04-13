package com.aua.museum.booking.mapping;
import com.aua.museum.booking.domain.GeneralInfo;
import com.aua.museum.booking.dto.GeneralInfoDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
@DecoratedWith(GeneralInfoMapperDecorator.class)
public interface GeneralInfoMapper {

    GeneralInfo toEntity(GeneralInfoDto generalInfoDto);
}
