package com.aua.museum.booking.service.impl;



import com.aua.museum.booking.domain.Event;
import com.aua.museum.booking.domain.EventType;
import com.aua.museum.booking.domain.User;
import com.aua.museum.booking.service.DownloadService;
import com.aua.museum.booking.service.EventService;
import com.aua.museum.booking.service.EventTypeService;
import com.aua.museum.booking.service.UserService;
import com.aua.museum.booking.util.GeneratePdfReport;
import com.aua.museum.booking.util.WriteData;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class DownloadServiceImpl implements DownloadService {

    private final UserService userService;
    private final EventService eventService;
    private final MessageSource messageSource;
    private final EventTypeService eventTypeService;

    @Override
    public void downloadCsv(String username, HttpServletResponse response,
                            HttpServletRequest request, Locale locale, String filename) throws IOException {
        List<Event> events = getEventsForDownloading(username, request);
        WriteData.generateReport(response.getWriter(), events, locale, messageSource, filename);
    }

    @Override
    public void downloadPdf(String username, HttpServletResponse response,
                            HttpServletRequest request, Locale locale) throws IOException {
        List<Event> eventsInRange = getEventsForDownloading(username, request);
        GeneratePdfReport.generateReport(eventsInRange, request, response, locale, messageSource);
    }

    @Override
    public List<Event> getEventsForDownloading(String username, HttpServletRequest request) {
        List<EventType> eventTypes = new ArrayList<>();
        List<EventType> defaultEventTypes = eventTypeService.getAllEventTypes();
        User user = userService.getUserByUsername(username);
        String startDateString = request.getParameter("date1");
        String endDateString = request.getParameter("date2");
        String eventFilters = request.getParameter("eventFilters");

        if (eventFilters != null) {
            List<String> eventFiltersInfo = Arrays.asList(eventFilters.split(","));
            eventTypes = extractEventTypes(eventFiltersInfo);
        }

        var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        var startDate = LocalDate.parse(startDateString, formatter);
        var endDate = LocalDate.parse(endDateString, formatter);

        if (request.getParameter("isFromMyActivities").equals("true")) {
            return eventTypes.size() != 0 ?
                    eventService.findByDateBetweenAndUser(startDate, endDate, user, eventTypes)
                    : eventService.findByDateBetweenAndUser(startDate, endDate, user, defaultEventTypes);
        }
        return eventTypes.size() != 0 ?
                eventService.findByDateBetween(startDate, endDate, eventTypes)
                : eventService.findByDateBetween(startDate, endDate, defaultEventTypes);
    }


    @Override
    public List<EventType> extractEventTypes(List<String> eventTypesInfo) {
        var eventTypes = new ArrayList<EventType>();
        eventTypesInfo
                .forEach(event -> {
                    switch (event) {
                        case "preschool":
                            eventTypes.add(eventTypeService.getEventTypeById(1));
                            break;
                        case "elementary":
                            eventTypes.add(eventTypeService.getEventTypeById(2));
                            break;
                        case "middle":
                            eventTypes.add(eventTypeService.getEventTypeById(3));
                            break;
                        case "high":
                            eventTypes.add(eventTypeService.getEventTypeById(4));
                            break;
                        case "students":
                            eventTypes.add(eventTypeService.getEventTypeById(5));
                            break;
                        case "individuals":
                            eventTypes.add(eventTypeService.getEventTypeById(6));
                            break;
                        case "event":
                            eventTypes.add(eventTypeService.getEventTypeById(7));
                            break;
                    }
                });
        return eventTypes;
    }
}
