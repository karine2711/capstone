package com.aua.museum.booking.controller;

import com.aua.museum.booking.domain.Event;
import com.aua.museum.booking.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
@RequestMapping(path = "waiting-list")
public class WaitingListController {

    private final EventService eventService;

    @Autowired
    public WaitingListController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ModelAndView getWaitingListPage(ModelAndView modelAndView) {
        final List<Event> activePreBookedEvents = eventService.getActiveUsersPreBookedEvents();
        final List<Event> blockedPreBookedEvents = eventService.getBlockedUsersPreBookedEvents();
        final Map<String, List<Event>> activePreBookedEventsByDate = eventsToMap(activePreBookedEvents);
        final Map<String, List<Event>> blockedPreBookedEventsByDate = eventsToMap(blockedPreBookedEvents);
        modelAndView.addObject("activePreBookedEvents", activePreBookedEventsByDate);
        modelAndView.addObject("blockedPreBookedEvents", blockedPreBookedEventsByDate);
        modelAndView.setViewName(Templates.WAITING_LIST.getName());
        return modelAndView;
    }

    @GetMapping("/events/active")
    @ResponseBody
    public ResponseEntity<Map<String, List<Map<Object, Object>>>> getActiveUsersPreBookedEvents() {
        final List<Event> preBookedEvents = eventService.getActiveUsersPreBookedEvents();
        final Map<String, List<Map<Object, Object>>> preBookedEventsByDate = eventsToRestMap(preBookedEvents, LocaleContextHolder.getLocale());
        return ResponseEntity.ok(preBookedEventsByDate);
    }

    @GetMapping("/events/blocked")
    @ResponseBody
    public ResponseEntity<Map<String, List<Map<Object, Object>>>> getBlockedUsersPreBookedEvents() {
        final List<Event> preBookedEvents = eventService.getBlockedUsersPreBookedEvents();
        final Map<String, List<Map<Object, Object>>> preBookedEventsByDate = eventsToRestMap(preBookedEvents, LocaleContextHolder.getLocale());
        return ResponseEntity.ok(preBookedEventsByDate);
    }

    private Map<String, List<Event>> eventsToMap(List<Event> events) {
        Map<String, List<Event>> timeEventMap = new HashMap<>();
        events.forEach(event -> {
            int minutes = event.getEventType().getDuration();
            String key = event.getDate().toString() + "T" + event.getTime().plusMinutes(minutes);
            List<Event> eventsForDate = timeEventMap.computeIfAbsent(key, k -> new ArrayList<>());
            eventsForDate.add(event);
        });
        return timeEventMap;
    }

    private Map<String, List<Map<Object, Object>>> eventsToRestMap(List<Event> events, Locale locale) {
        Map<String, List<Map<Object, Object>>> timeEventMap = new TreeMap<>(Comparator.reverseOrder());
        events.stream().map(event -> eventService.eventToMap(event, locale)).forEach(event -> {
            String key = (String) event.get("start");
            List<Map<Object, Object>> eventsForDate = timeEventMap.computeIfAbsent(key, k -> new ArrayList<>());
            eventsForDate.add(event);
        });
        return timeEventMap;
    }
}
