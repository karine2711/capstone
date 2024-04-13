package com.aua.museum.booking.service;

import com.aua.museum.booking.domain.*;
import com.aua.museum.booking.domain.Event;
import com.aua.museum.booking.domain.EventLite;
import com.aua.museum.booking.domain.EventType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public interface EventService {
    Event createEvent(Event event);

    Event getEventById(long id);

    List<EventLite> getAllEventLites();

    List<Event> getAllEvents();

    List<EventLite> getActiveUsersPreBookedEvents();

    List<EventLite> getBlockedUsersPreBookedEvents();

    void removeEvent(Event event);

    void confirmEvent(Event event);

    void changeEventToBooked(Event event);

    void checkAndChangeToPreBookedAfterReschedule(Event event);

    void rescheduleEvent(Long eventId, LocalDate date, LocalTime time);

    Map<Object, Object> eventToMap(EventLite event, Locale locale);

    String extractEventPhoto(Event event);

    List<Event> findByDateBetween(LocalDate startDate, LocalDate endDate, List<EventType> eventTypes);

    List<Event> findByDateBetweenAndUser(LocalDate startDate, LocalDate endDate, User user, List<EventType> eventTypes);

    Set<LocalTime> getFreeTimesByDateAndEvent(LocalDate date, Event event);

    Set<LocalTime> getFreeTimesByDateAndEventType(LocalDate date, EventType eventType, Integer groupSize);

}