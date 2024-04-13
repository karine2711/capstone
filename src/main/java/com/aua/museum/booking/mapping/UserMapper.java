package com.aua.museum.booking.mapping;

import com.aua.museum.booking.domain.User;
import com.aua.museum.booking.dto.UserDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
@DecoratedWith(UserMapperDecorator.class)
public interface UserMapper {



    @Mapping(target = "fullName", source = "fullName")
    abstract User toEntity(UserDto dto);

}
