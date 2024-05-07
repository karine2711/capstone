package com.aua.museum.booking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.aua.museum.booking.domain.Event;
import com.aua.museum.booking.domain.EventLite;
import com.aua.museum.booking.domain.EventState;
import com.aua.museum.booking.domain.EventType;
import com.aua.museum.booking.domain.Notification;
import com.aua.museum.booking.domain.Role;
import com.aua.museum.booking.domain.User;
import com.aua.museum.booking.dto.EventDto;
import com.aua.museum.booking.mapping.EventMapper;
import com.aua.museum.booking.security.UserDetailsServiceImpl;
import com.aua.museum.booking.service.EventService;
import com.aua.museum.booking.service.EventTypeService;
import com.aua.museum.booking.service.NotificationService;
import com.aua.museum.booking.service.UserService;
import com.aua.museum.booking.service.notifications.FCMService;
import com.aua.museum.booking.util.DateTimeUtil;
import com.aua.museum.booking.util.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/event", "/event/{eventId}"})
@RequiredArgsConstructor
public class EventController {
    private final EventTypeService eventTypeService;
    private final EventService eventService;
    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;
    private final EventMapper eventMapper;
    private final NotificationService notificationService;
    private final FCMService fcmService;

    @GetMapping
    public ModelAndView getAddActivityPage(Principal principal, @PathVariable(value = "eventId", required = false) Integer eventId) {
        ModelAndView modelAndView = new ModelAndView();
        UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
        User user = userService.getUserByUsername(principal.getName());
        Event event = new Event();
        if (eventId != null && userDetails.getAuthorities().contains(new SimpleGrantedAuthority(Role.ADMIN_ROLE.name()))) {
            event = eventService.getEventById(eventId);
            modelAndView.addObject("currentEvent", event);
            modelAndView.addObject("currentEventTypeId", event.getEventType().getId());
            modelAndView.addObject("currentEventDate", event.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

            modelAndView.addObject("currentEventTime", event.getTime());
            modelAndView.addObject("currentEventEndTime", event.getTime().plusMinutes(event.getEventType().getDuration()));
            modelAndView.addObject("currentEventDescAm", event.getDescription_AM());
            modelAndView.addObject("currentEventDescRu", event.getDescription_RU());
            modelAndView.addObject("currentEventDescEn", event.getDescription_EN());

            modelAndView.addObject("currentEventTitleAm", event.getTitle_AM());
            modelAndView.addObject("currentEventTitleRu", event.getTitle_RU());
            modelAndView.addObject("currentEventTitleEn", event.getTitle_EN());
            modelAndView.addObject("currentPhotoExists", event.getEventPhoto() != null);
            modelAndView.addObject("currentEventPhoto", eventService.extractEventPhoto(event));
        }
        event.setUser(user);
        modelAndView.setViewName(com.aua.museum.booking.controller.Templates.ADD_ACTIVITY.getName());
        modelAndView.addObject("eventTypes", eventTypeService.getAllEventTypes());
        modelAndView.addObject("event", event);
        modelAndView.addObject("profileAvatar", userService.extractAvatarPicture(user));
        modelAndView.setStatus(HttpStatus.OK);

        return modelAndView;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity addEvent(@RequestPart("event") @Valid EventDto eventDto, @RequestPart(value = "file", required = false) MultipartFile file, @PathVariable(value = "eventId", required = false) Long eventId, Principal principal) {
        Event event = eventMapper.toEntity(eventDto);
        event.setId(eventId);
        if (file != null) {
            try {
                byte[] image = ImageService.extractImageFromFile(file);
                event.setEventPhoto(ImageService.compress(image));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            event.setEventPhoto(null);
        }
        event.setUser(userService.getUserByUsername(principal.getName()));
        Event savedEvent = eventService.createEvent(event);
        if (event.getEventState() == EventState.PRE_BOOKED) {
            final Notification preBookedUser = notificationService.createPreBookedUser(savedEvent.getUser(), savedEvent);
            notificationService.save(preBookedUser);
            List<User> admins = userService.getAllAdmins();
            admins.forEach(admin -> {
                final Notification preBookedAdminNotification = notificationService.createPreBookedAdmin(admin, savedEvent);
                notificationService.save(preBookedAdminNotification);
                if (admin.getToken() != null) {
                    try {
                        fcmService.sendNotification(admin.getToken(), preBookedAdminNotification);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (!savedEvent.getUser().isAdmin() && savedEvent.isWithin24Hours()) {
            final Notification reminder = notificationService.createConfirm(savedEvent.getUser(), savedEvent);
            notificationService.save(reminder);
        }
        return ResponseEntity.ok().build();
    }

    //todo: fix
//    @GetMapping("/photo/{eventId}")
//    public ResponseEntity<Object> getUserPhoto(@PathVariable Long eventId) {
//        final Event event = eventService.getEventById(eventId);
//        final String picture = eventService.extractEventPhoto(event);
//        return picture == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(picture);
//    }

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
        List<EventLite> allEvents = eventService.getAllEventLites();
        final List<Map<Object, Object>> events = allEvents.stream().map(event -> eventService.eventToMap(event, locale)).collect(Collectors.toList());
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = {"/all", "/all/{path}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Map<Object, Object>>> getAllEvents(Locale locale, @PathVariable(required = false) String path, Principal principal) {
        List<EventLite> allEvents = eventService.getAllEventLites();
        if (path.equals("myActivities"))
            allEvents = allEvents.stream().filter(event -> event.getUser().getUsername().equalsIgnoreCase(principal.getName())).collect(Collectors.toList());

        Map<LocalDateTime, EventLite> xex = new HashMap<>();
        for (EventLite event : allEvents) {
            LocalDateTime time = DateTimeUtil.getDateTime(event.getDate(), event.getTime());
            if (!xex.containsKey(time) || !xex.get(time).getUser().getUsername().equals(principal.getName())) {
                xex.put(time, event);
            }

        }
        List<EventLite> filtered = new ArrayList<>(xex.values());

        final List<Map<Object, Object>> events = filtered.stream().map(event -> eventService.eventToMap(event, locale)).collect(Collectors.toList());
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping(value = "/getEventTypes")
    public ResponseEntity<Map<String, String>> getEventTypes() {
        Map<String, String> eventTypes = new LinkedHashMap<>();
        eventTypeService.getAllEventTypes()
                .forEach((t) -> eventTypes.put(t.getDisplayValue_EN(), eventTypeService.getEventTypeValueFromLocale(t)));
        return ResponseEntity.ok().body(eventTypes);
    }

    @PostMapping(value = "/reschedule", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity rescheduleTime(@RequestBody Map<String, String> body) {
        Long id = Long.parseLong(body.get("eventId"));
        Event rescheduledEvent = eventService.getEventById(id);
        eventService.checkAndChangeToPreBookedAfterReschedule(rescheduledEvent);
        eventService.rescheduleEvent(Long.parseLong(body.get("eventId")),
                LocalDate.parse(body.get("date")), LocalTime.parse(body.get("time")));
        notificationService.getUsersAllNotifications(rescheduledEvent.getUser()).forEach(n -> {
            if (n.getEvent().equals(rescheduledEvent)){
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
                if (admin.getToken() != null) {
                    try {
                        fcmService.sendNotification(admin.getToken(), preBookedAdminNotification);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (!rescheduledEvent.getUser().isAdmin()) {
            if (rescheduledEvent.isWithin24Hours()) {
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
    public ResponseEntity deleteEvent(@PathVariable Long id) {
        eventService.removeEvent(eventService.getEventById(id));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/confirm/{id}")
    public ResponseEntity confirmEvent(@PathVariable Long id) {
        eventService.confirmEvent(eventService.getEventById(id));
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "/book", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity changeEventToBooked(@RequestBody List<Long> ids) {
        ids.forEach(id -> {
            Event event = eventService.getEventById(id);
            eventService.changeEventToBooked(event);
            final Notification preBookedConfirmNotification = notificationService.createPreBookedActivityConfirmed(event.getUser(), event);
            notificationService.save(preBookedConfirmNotification);
        });
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/reschedule/allChosen", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity reschedulePreBookedEvent(@RequestBody List<Map<String, String>> body) throws JsonProcessingException {
        body.forEach(event -> {
            Long id = Long.parseLong(event.get("eventId"));
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
    public ResponseEntity deletePreBookedEvent(@RequestBody List<Long> ids) {
        ids.forEach(id -> {
            Event event = eventService.getEventById(id);
            final Notification preBookedDeleteNotification = notificationService.createPreBookedActivityDeleted(event.getUser(), event);
            notificationService.save(preBookedDeleteNotification);
            eventService.removeEvent(event);
        });
        return ResponseEntity.ok().build();
    }
}
