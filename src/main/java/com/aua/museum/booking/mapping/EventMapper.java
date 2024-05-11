package com.aua.museum.booking.mapping;

import com.aua.museum.booking.converter.DateConverter;
import com.aua.museum.booking.domain.Event;
import com.aua.museum.booking.dto.EventDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = DateConverter.class)
@DecoratedWith(EventMapperDecorator.class)

public interface EventMapper {

    @Mapping(ignore = true, target = "eventType")
    Event toEntity(EventDto dto);

}
