package com.aua.museum.booking.controller;

import com.aua.museum.booking.domain.EventLite;
import com.aua.museum.booking.service.EventService;
import com.aua.museum.booking.service.NotificationService;
import com.aua.museum.booking.service.UserService;
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
    @Autowired
    private UserService userService;
    @Autowired
    private EventService eventService;
    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ModelAndView getWaitingListPage(ModelAndView modelAndView) {
        final List<EventLite> activePreBookedEvents = eventService.getActiveUsersPreBookedEvents();
        final List<EventLite> blockedPreBookedEvents = eventService.getBlockedUsersPreBookedEvents();
        final Map<String, List<EventLite>> activePreBookedEventsByDate = eventsToMap(activePreBookedEvents);
        final Map<String, List<EventLite>> blockedPreBookedEventsByDate = eventsToMap(blockedPreBookedEvents);
        modelAndView.addObject("activePreBookedEvents", activePreBookedEventsByDate);
        modelAndView.addObject("blockedPreBookedEvents", blockedPreBookedEventsByDate);
        modelAndView.setViewName(Templates.WAITING_LIST.getName());
        return modelAndView;
    }

    @GetMapping("/events/active")
    @ResponseBody
    public ResponseEntity<Map<String, List<Map<Object, Object>>>> getActiveUsersPreBookedEvents() {
        final List<EventLite> preBookedEvents = eventService.getActiveUsersPreBookedEvents();
        final Map<String, List<Map<Object, Object>>> preBookedEventsByDate = eventsToRestMap(preBookedEvents, LocaleContextHolder.getLocale());
        return ResponseEntity.ok(preBookedEventsByDate);
    }

    @GetMapping("/events/blocked")
    @ResponseBody
    public ResponseEntity<Map<String, List<Map<Object, Object>>>> getBlockedUsersPreBookedEvents() {
        final List<EventLite> preBookedEvents = eventService.getBlockedUsersPreBookedEvents();
        final Map<String, List<Map<Object, Object>>> preBookedEventsByDate = eventsToRestMap(preBookedEvents, LocaleContextHolder.getLocale());
        return ResponseEntity.ok(preBookedEventsByDate);
    }

    private Map<String, List<EventLite>> eventsToMap(List<EventLite> events) {
        Map<String, List<EventLite>> timeEventMap = new HashMap<>();
        events.stream().forEach(event -> {
            int minutes = event.getEventType().getDuration();
            String key = event.getDate().toString() + "T" + event.getTime().plusMinutes(minutes);
            List<EventLite> eventsForDate = timeEventMap.get(key);
            if (eventsForDate == null) {
                eventsForDate = new ArrayList<>();
                timeEventMap.put(key, eventsForDate);
            }
            eventsForDate.add(event);
        });
        return timeEventMap;
    }

    private Map<String, List<Map<Object, Object>>> eventsToRestMap(List<EventLite> events, Locale locale) {
        Map<String, List<Map<Object, Object>>> timeEventMap = new TreeMap<>(Comparator.reverseOrder());
        events.stream().map(event -> eventService.eventToMap(event, locale)).forEach(event -> {
            String key = (String) event.get("start");
            List<Map<Object, Object>> eventsForDate = timeEventMap.get(key);
            if (eventsForDate == null) {
                eventsForDate = new ArrayList<>();
                timeEventMap.put(key, eventsForDate);
            }
            eventsForDate.add(event);
        });
        return timeEventMap;
    }
}
