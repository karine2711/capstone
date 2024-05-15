package com.aua.museum.booking.controller;

import com.aua.museum.booking.domain.*;
import com.aua.museum.booking.dto.EventDto;
import com.aua.museum.booking.mapping.EventMapper;
import com.aua.museum.booking.security.UserDetailsServiceImpl;
import com.aua.museum.booking.service.EventService;
import com.aua.museum.booking.service.EventTypeService;
import com.aua.museum.booking.service.NotificationService;
import com.aua.museum.booking.service.UserService;
import com.aua.museum.booking.util.DateTimeUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/event"})
@RequiredArgsConstructor
public class EventController {
    private final EventTypeService eventTypeService;
    private final EventService eventService;
    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;
    private final EventMapper eventMapper;
    private final NotificationService notificationService;

    @GetMapping
    public ModelAndView getAddActivityPage(Principal principal) {
        ModelAndView modelAndView = new ModelAndView();
        User user = userService.getUserByUsername(principal.getName());
        Event event = new Event();
        event.setUser(user);
        modelAndView.setViewName(com.aua.museum.booking.controller.Templates.ADD_ACTIVITY.getName());
        modelAndView.addObject("eventTypes", eventTypeService.getAllEventTypes());
        modelAndView.addObject("event", event);
        modelAndView.addObject("profileAvatar", userService.extractAvatarPicture(user));
        modelAndView.setStatus(HttpStatus.OK);
        return modelAndView;
    }

    @GetMapping("/{eventId}")
    public ModelAndView getUpdateEventPage(Principal principal, @PathVariable(value = "eventId") Integer eventId, HttpServletRequest request) {
            ModelAndView modelAndView = new ModelAndView();
            UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
            User user = userService.getUserByUsername(principal.getName());
            Event event = eventService.getEventById(eventId);
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority(Role.ADMIN_ROLE.name())) && event.getEventType().getId().equals(7)) {
            modelAndView.addObject("currentEvent", event);
            modelAndView.addObject("currentEventTypeId", event.getEventType().getId());
            modelAndView.addObject("currentEventDate", event.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

            modelAndView.addObject("currentEventTime", event.getTime());
            modelAndView.addObject("currentEventEndTime", event.getTime().plusMinutes(event.getEventType().getDuration()));
            modelAndView.addObject("currentEventDescAm", event.getDescriptionAM());
            modelAndView.addObject("currentEventDescRu", event.getDescriptionRU());
            modelAndView.addObject("currentEventDescEn", event.getDescriptionEN());

            modelAndView.addObject("currentEventTitleAm", event.getTitleAM());
            modelAndView.addObject("currentEventTitleRu", event.getTitleRU());
            modelAndView.addObject("currentEventTitleEn", event.getTitleEN());

            event.setUser(user);
            modelAndView.setViewName(com.aua.museum.booking.controller.Templates.ADD_ACTIVITY.getName());
            modelAndView.addObject("eventTypes", eventTypeService.getAllEventTypes());
            modelAndView.addObject("event", event);
            modelAndView.addObject("profileAvatar", userService.extractAvatarPicture(user));
            modelAndView.setStatus(HttpStatus.OK);

            return modelAndView;
        } else {
            return new ModelAndView(new RedirectView("/event", true));
        }
    }

    @PostMapping
    public ResponseEntity<Void> addEvent(@RequestPart("event") @Valid EventDto eventDto, Principal principal) {
        Event event = eventMapper.toEntity(eventDto);
        event.setUser(userService.getUserByUsername(principal.getName()));
        Event savedEvent = eventService.createEvent(event);
        if (event.getEventState() == EventState.PRE_BOOKED) {
            final Notification preBookedUser = notificationService.createPreBookedUser(savedEvent.getUser(), savedEvent);
            notificationService.save(preBookedUser);
            List<User> admins = userService.getAllAdmins();
            admins.forEach(admin -> {
                final Notification preBookedAdminNotification = notificationService.createPreBookedAdmin(admin, savedEvent);
                notificationService.save(preBookedAdminNotification);
            });
        } else if (!savedEvent.getUser().isAdmin() && savedEvent.isTomorrow()) {
            final Notification reminder = notificationService.createConfirm(savedEvent.getUser(), savedEvent);
            notificationService.save(reminder);
        }
        return ResponseEntity.ok().build();
    }


    @PostMapping("/{eventId}")
    public ResponseEntity<Void> updateEvent(@RequestPart("event") @Valid EventDto eventDto, @PathVariable(value = "eventId") Long eventId, Principal principal) {
        Event event = eventMapper.toEntity(eventDto);
        event.setId(eventId);
        event.setUser(userService.getUserByUsername(principal.getName()));
        eventService.createEvent(event);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/free-times/{date}/{id}")
    public Set<LocalTime> getFreeTimesForUpdate(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                @PathVariable(value = "id", required = false) Integer id) {
        Event event = eventService.getEventById(id);
        return eventService.getFreeTimesByDateAndEvent(date, event);
    }

    @GetMapping("/free-times/{date}/{eventType}/{groupSize}")
    public Set<LocalTime> getFreeTimesForCreate(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                @PathVariable("eventType") int eventTypeId, @PathVariable(value = "groupSize", required = false) Integer groupSize) {
        EventType type = eventTypeService.getEventTypeById(eventTypeId);
        return eventService.getFreeTimesByDateAndEventType(date, type, groupSize);

    }

    @GetMapping(value = {"/allWithoutFilter",})
    public ResponseEntity<List<Map<Object, Object>>> getAllEvents(Locale locale) {
        List<Event> allEvents = eventService.getAllEvents();
        final List<Map<Object, Object>> events = allEvents.stream().map(event -> eventService.eventToMap(event, locale)).collect(Collectors.toList());
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = {"/all", "/all/{path}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Map<Object, Object>>> getAllEvents(Locale locale, @PathVariable(required = false) String path, Principal principal) {
        List<Event> allEvents = eventService.getAllEvents();
        if (path.equals("myActivities"))
            allEvents = allEvents.stream().filter(event -> event.getUser().getUsername().equalsIgnoreCase(principal.getName())).toList();

        Map<LocalDateTime, Event> xex = new HashMap<>();
        for (Event event : allEvents) {
            LocalDateTime time = DateTimeUtil.getDateTime(event.getDate(), event.getTime());
            if (!xex.containsKey(time) || !xex.get(time).getUser().getUsername().equals(principal.getName())) {
                xex.put(time, event);
            }

        }
        List<Event> filtered = new ArrayList<>(xex.values());

        final List<Map<Object, Object>> events = filtered.stream().map(event -> eventService.eventToMap(event, locale)).collect(Collectors.toList());
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping(value = "/getEventTypes")
    public ResponseEntity<Map<String, String>> getEventTypes() {
        Map<String, String> eventTypes = new LinkedHashMap<>();
        eventTypeService.getAllEventTypes()
                .forEach((t) -> eventTypes.put(t.getDisplayValueEN(), eventTypeService.getEventTypeValueFromLocale(t)));
        return ResponseEntity.ok().body(eventTypes);
    }

    @PostMapping(value = "/reschedule", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> rescheduleTime(@RequestBody Map<String, String> body) {
        long id = Long.parseLong(body.get("eventId"));
        Event rescheduledEvent = eventService.getEventById(id);
        eventService.checkAndChangeToPreBookedAfterReschedule(rescheduledEvent);
        eventService.rescheduleEvent(Long.parseLong(body.get("eventId")),
                LocalDate.parse(body.get("date")), LocalTime.parse(body.get("time")));
        notificationService.getUsersAllNotifications(rescheduledEvent.getUser()).forEach(n -> {
            if (n.getEvent().equals(rescheduledEvent)) {
                notificationService.delete(n);
            }
        });
        if (rescheduledEvent.getEventState() == EventState.PRE_BOOKED) {
            final Notification preBookedUser = notificationService.createPreBookedUser(rescheduledEvent.getUser(), rescheduledEvent);
            notificationService.save(preBookedUser);
            List<User> admins = userService.getAllAdmins();
            admins.forEach(admin -> {
                final Notification preBookedAdminNotification = notificationService.createPreBookedAdmin(admin, rescheduledEvent);
                notificationService.findAndDeleteDuplicateOfNotification(preBookedAdminNotification, admin);
                notificationService.save(preBookedAdminNotification);
            });
        } else if (!rescheduledEvent.getUser().isAdmin()) {
            if (rescheduledEvent.isTomorrow()) {
                final Notification reminder = notificationService.createConfirm(rescheduledEvent.getUser(), rescheduledEvent);
                notificationService.save(reminder);
            } else {
                final Notification reminderAboutConfirm = notificationService.createReminderAboutConfirm(rescheduledEvent.getUser(), rescheduledEvent);
                notificationService.save(reminderAboutConfirm);
            }
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.removeEvent(eventService.getEventById(id));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/confirm/{id}")
    public ResponseEntity<Void> confirmEvent(@PathVariable Long id) {
        eventService.confirmEvent(eventService.getEventById(id));
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "/book", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> changeEventToBooked(@RequestBody List<Long> ids) {
        ids.forEach(id -> {
            Event event = eventService.getEventById(id);
            eventService.changeEventToBooked(event);
            final Notification preBookedConfirmNotification = notificationService.createPreBookedActivityConfirmed(event.getUser(), event);
            notificationService.save(preBookedConfirmNotification);
        });
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/reschedule/allChosen", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> reschedulePreBookedEvent(@RequestBody List<Map<String, String>> body) {
        body.forEach(event -> {
            long id = Long.parseLong(event.get("eventId"));
            Event rescheduledEvent = eventService.getEventById(id);
            eventService.rescheduleEvent(id,
                    LocalDate.parse(event.get("date")), LocalTime.parse(event.get("time")));
            final Notification preBookedRescheduleNotification = notificationService.createPreBookedActivityRescheduled(rescheduledEvent.getUser(), rescheduledEvent);
            notificationService.findAndDeleteDuplicateOfNotification(preBookedRescheduleNotification, rescheduledEvent.getUser());
            notificationService.save(preBookedRescheduleNotification);
        });
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/delete/allChosen", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deletePreBookedEvent(@RequestBody List<Long> ids) {
        ids.forEach(id -> {
            Event event = eventService.getEventById(id);
            final Notification preBookedDeleteNotification = notificationService.createPreBookedActivityDeleted(event.getUser(), event);
            notificationService.save(preBookedDeleteNotification);
            eventService.removeEvent(event);
        });
        return ResponseEntity.ok().build();
    }
}
