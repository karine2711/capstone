package com.aua.museum.booking.service;


import com.aua.museum.booking.domain.EventType;

import java.util.List;

public interface EventTypeService {

    EventType getEventTypeById(int id);

    List<EventType> getAllEventTypes();

    String getEventTypeValueFromLocale(EventType eventType);

    List<String> getAllEventTypeValues();

    EventType getEventTypeByValue(String eventTypeValue);
}
