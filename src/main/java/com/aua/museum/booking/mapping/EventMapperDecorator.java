package com.aua.museum.booking.mapping;

import com.aua.museum.booking.domain.Event;
import com.aua.museum.booking.dto.EventDto;
import com.aua.museum.booking.service.EventTypeService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;


@NoArgsConstructor
public class EventMapperDecorator implements com.aua.museum.booking.mapping.EventMapper {


    private com.aua.museum.booking.mapping.EventMapper eventMapper;
    @Autowired
    private EventTypeService eventTypeService;

    @Autowired
    public void setEventMapper(com.aua.museum.booking.mapping.EventMapper eventMapper) {
        this.eventMapper = eventMapper;
    }

    @Override
    public Event toEntity(EventDto dto) {
        Event event = eventMapper.toEntity(dto);

        event.setEventType(eventTypeService.getEventTypeById(dto.getEventType()));
        return event;
    }

}
