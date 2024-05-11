package com.aua.museum.booking.service.impl;


import com.aua.museum.booking.domain.Event;
import com.aua.museum.booking.domain.EventType;
import com.aua.museum.booking.domain.Notification;
import com.aua.museum.booking.domain.User;
import com.aua.museum.booking.exception.notfound.EventNotFoundException;
import com.aua.museum.booking.repository.EventRepository;
import com.aua.museum.booking.service.EventService;
import com.aua.museum.booking.service.EventTypeService;
import com.aua.museum.booking.service.UserService;
import com.aua.museum.booking.service.notifications.FCMService;
import com.aua.museum.booking.util.DateTimeUtil;
import com.aua.museum.booking.util.ZonedDateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.aua.museum.booking.domain.EventState.BOOKED;
import static com.aua.museum.booking.domain.EventState.PRE_BOOKED;
import static com.aua.museum.booking.util.ZonedDateTimeUtil.getArmNowDateTime;
import static com.aua.museum.booking.util.ZonedDateTimeUtil.getArmNowTime;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository repository;
    private final NotificationServiceImpl notificationService;
    private final FCMService fcmService;
    private final UserService userService;
    private final DateTimeUtil dateTimeUtil;
    private final EventTypeService eventTypeService;

    @Override
    public Event createEvent(Event event) {
        if (event.getEventType().getId() == 7) {
            return doRescheduleEvent(event);
        }
        Integer groupSize = event.getGroupSize();
        if (groupSize > 25) {
            event.setEventState(PRE_BOOKED);
        } else {
            event.setEventState(BOOKED);
        }
        return repository.save(event);

    }

    private void moveEvent(Event eventToMove, Event rescheduledEvent, List<Event> bookedDates) {

        final int minutes = 15;
        int eventDuration = eventToMove.getEventType().getDuration();
        List<LocalTime> freeTimes;
        LocalTime nextPossibleFreeSlot;
        if (eventToMove.getDate().equals(rescheduledEvent.getDate())) {
            List<Event> bookedDatesFiltered = bookedDates;
            Map<LocalTime, Integer> bookedTimes = new HashMap<>();
            if (eventToMove.getEventType().getId() != 7) {
                bookedDates.stream()
                        .filter(e -> e.getEventType().equals(eventToMove.getEventType()))
                        .forEach(e -> {
                            if (bookedTimes.containsKey(e.getTime())) {
                                bookedTimes.computeIfPresent(e.getTime(), (k, v) -> v + e.getGroupSize());
                            } else {
                                bookedTimes.put(e.getTime(), e.getGroupSize());
                            }
                        });
                bookedDatesFiltered = bookedDates.stream()
                        .filter(e -> !((e.getEventType() == eventToMove.getEventType()) && ((e.getTime()) != null && bookedTimes.get(e.getTime()) + eventToMove.getGroupSize() < 35) && e.getTime().isAfter(getArmNowTime())))
                        .collect(Collectors.toList());
            }
            freeTimes = dateTimeUtil.getTimesForMove(bookedDatesFiltered, eventToMove.getDate(), eventToMove);
            nextPossibleFreeSlot = rescheduledEvent.getTime().plusMinutes(rescheduledEvent.getEventType().getDuration());
        } else {
            freeTimes = new ArrayList<>(getFreeTimesByDateAndEvent(eventToMove.getDate(), eventToMove));
            nextPossibleFreeSlot = LocalTime.of(10, 0);
        }
        LocalTime endTime = nextPossibleFreeSlot;

        while (endTime.isBefore(LocalTime.of(16, 0))) {
            if (freeTimes.contains(nextPossibleFreeSlot)) {
                eventToMove.setTime(nextPossibleFreeSlot);
                repository.save(eventToMove);

                if (bookedDates != null) {
                    bookedDates.add(eventToMove);
                }
                final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                User user = userService.getUserByUsername(authentication.getName());
                if (user.isAdmin() && !eventToMove.getUser().isAdmin()) {
                    final Notification reschedulerNotification = notificationService.createReschedule(eventToMove.getUser(), eventToMove);
                    notificationService.findAndDeleteDuplicateOfNotification(reschedulerNotification, eventToMove.getUser());
                    notificationService.save(reschedulerNotification);
                    if (eventToMove.getUser().getToken() != null) {
                        try {
                            fcmService.sendNotification(eventToMove.getUser(), reschedulerNotification);
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return;
            } else {
                nextPossibleFreeSlot = nextPossibleFreeSlot.plusMinutes(minutes);
                endTime = nextPossibleFreeSlot.plusMinutes(eventDuration);
            }
        }
        if (eventToMove.getDate().getDayOfWeek() == DayOfWeek.SATURDAY) {
            eventToMove.setDate(eventToMove.getDate().plusDays(2));
        } else {
            eventToMove.setDate(eventToMove.getDate().plusDays(1));
        }
        moveEvent(eventToMove, rescheduledEvent, null);
    }

    @Override
    public Event getEventById(long id) {
        return repository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
    }

    @Override
    public List<Event> getActiveUsersPreBookedEvents() {
        return repository.findActiveUsersPreBooked()
                .stream()
                .filter(this::eventIsNotPassed)
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> getBlockedUsersPreBookedEvents() {
        return repository.findActiveUsersPreBooked()
                .stream()
                .filter(this::eventIsNotPassed)
                .collect(Collectors.toList());
    }

    private boolean eventIsNotPassed(Event event) {
        return LocalDateTime.of(event.getDate(), event.getTime()).isAfter(ZonedDateTimeUtil.getArmNowDateTime());
    }

    @Override
    public List<Event> getAllEvents() {
        return repository.findAll();
    }

    public List<Event> getBookedEventsByDate(LocalDate date) {
        return repository.findByDateAndTimeBetween(date, LocalTime.of(0, 0), LocalTime.of(23, 59));
    }

    //todo: is needed
    @Override
    public void removeEvent(Event event) {
        event.getNotifications().forEach(notificationService::delete);
        repository.delete(event);
    }

    @Override
    public void confirmEvent(Event event) {
        event.setConfirmed();
        repository.save(event);
    }

    @Override
    public void changeEventToBooked(Event event) {
        event.setEventState(BOOKED);
        repository.save(event);
    }

    @Override
    public void checkAndChangeToPreBookedAfterReschedule(Event event) {
        if (event.getEventType().getId() != 7 && event.getEventState() != PRE_BOOKED && event.getGroupSize() < 10) {
            event.setEventState(PRE_BOOKED);
        }
        repository.save(event);
    }


    @Override
    public Set<LocalTime> getFreeTimesByDateAndEventType(LocalDate date, EventType eventType, Integer groupSize) {
        List<Event> bookedEventsByDate = getBookedEventsByDate(date);
        Set<LocalTime> times = new TreeSet<>(dateTimeUtil.getTimes(bookedEventsByDate, eventType, date));
        times.addAll(getCoHostingHours(eventType, groupSize, bookedEventsByDate));
        return times;
    }

    private Set<LocalTime> getCoHostingHours(EventType eventType, Integer groupSize, List<Event> bookedEventsByDate) {
        Map<LocalTime, Integer> events = new HashMap<>();
        if (eventType.getId() != 7 && groupSize < 35) {
            bookedEventsByDate
                    .stream()
                    .filter(e -> e.getEventType().equals(eventType))
                    .filter(e -> e.getGroupSize() <= 35 - groupSize)
                    .filter(e -> DateTimeUtil.getDateTime(e.getDate(), e.getTime()).isAfter(getArmNowDateTime()))
                    .forEach(e -> addToMap(events, e));
        }
        return events
                .keySet()
                .parallelStream()
                .filter(time -> events.get(time) + groupSize < 35)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<LocalTime> getFreeTimesByDateAndEvent(LocalDate date, Event event) {
        List<Event> bookedEventsByDate = getBookedEventsByDate(date);
        Set<LocalTime> times = new TreeSet<>(dateTimeUtil.getTimesForMove(bookedEventsByDate, date, event));
        times.addAll(getCoHostingHours(event.getEventType(), event.getGroupSize(), bookedEventsByDate));
        return times;
    }

    private void addToMap(Map<LocalTime, Integer> events, Event e) {
        if (events.containsKey(e.getTime())) {
            events.computeIfPresent(e.getTime(),(time,size) -> size+e.getGroupSize());
        } else {
            events.put(e.getTime(), e.getGroupSize());
        }
    }


    @Override
    public List<Event> findByDateBetween(LocalDate startDate, LocalDate endDate, List<EventType> eventTypes) {
        return repository.findByDateBetweenAndEventTypeInOrderByDateAscTimeAsc(startDate, endDate, eventTypes);
    }

    @Override
    public List<Event> findByDateBetweenAndUser(LocalDate startDate, LocalDate endDate, User
            user, List<EventType> eventTypes) {
        return repository.findByDateBetweenAndUserAndEventTypeInOrderByDateAscTimeAsc(startDate, endDate, user, eventTypes);
    }

    @Override
    public void rescheduleEvent(Long eventId, LocalDate date, LocalTime time) {
        Event event = getEventById(eventId);

        event.setDate(date);
        event.setTime(time);
        if (event.getEventType().getId() != 7) {
            repository.save(event);
        } else {
            doRescheduleEvent(event);
        }
    }

    private Event doRescheduleEvent(Event rescheduledEvent) {
        repository.save(rescheduledEvent);
        List<Event> events = repository.findByDateAndTimeBetween(
                rescheduledEvent.getDate().toString(),
                rescheduledEvent.getTime().minusMinutes(120).toString(),
                rescheduledEvent.getTime().plusMinutes(120).toString());


        List<Event> eventsToBeMoved = events.stream()
                .filter(e -> checkIfOverlaps(rescheduledEvent, e))
                .toList();

        List<Event> bookedEventsByDate = getBookedEventsByDate(rescheduledEvent.getDate());

        List<LocalTime> toBeMovedTimes = eventsToBeMoved.stream()
                .map(Event::getTime)
                .toList();
        List<Event> bookedDates = bookedEventsByDate.stream()
                .filter(e -> !toBeMovedTimes.contains(e.getTime()) || e.getId().equals(rescheduledEvent.getId()))
                .collect(Collectors.toList());

        eventsToBeMoved.forEach(eventToMove -> moveEvent(eventToMove, rescheduledEvent, bookedDates));
        return repository.save(rescheduledEvent);
    }


    private Boolean checkIfOverlaps(Event newEvent, Event oldEvent) {
        if (oldEvent.equals(newEvent)) {
            return false;
        }
        if (newEvent.getEventType().getId() != 7
                && newEvent.getEventType().getId().equals(oldEvent.getEventType().getId())
                && newEvent.getGroupSize() + oldEvent.getGroupSize() <= 35) {
            return false;
        }
        LocalTime oldStartTime = oldEvent.getTime();
        LocalTime oldEndTime = oldStartTime.plusMinutes(oldEvent.getEventType().getDuration());

        LocalTime newStartTime = newEvent.getTime();
        LocalTime newEndTime = newStartTime.plusMinutes(newEvent.getEventType().getDuration());

        if (oldStartTime.equals(newStartTime) && oldEndTime.equals(newEndTime)) {
            return true;
        }

        if (isWithinTime(oldStartTime, newStartTime, newEndTime) || isWithinTime(newStartTime, oldStartTime, oldEndTime)) {
            return true;
        }
        return isWithinTime(oldEndTime, newStartTime, newEndTime) || isWithinTime(newEndTime, oldStartTime, oldEndTime);

    }

    @Override
    public Map<Object, Object> eventToMap(Event event, Locale locale) {
        Map<Object, Object> map = new HashMap<>();
        map.put("type", event.getEventType().getDisplayValue_EN().toLowerCase());

        map.put("id", event.getId());
        map.put("school", event.getSchool());
        map.put("group", event.getGroup());
        map.put("groupSize", event.getGroupSize());
        map.put("start", event.getDate().toString() + "T" + event.getTime().toString());

        final int minutes = event.getEventType().getDuration();

        map.put("end", event.getDate().toString() + "T" + event.getTime().plusMinutes(minutes));
        map.put("count", event.getGroupSize());
        map.put("eventTypeId", event.getEventType().getId());
        map.put("title", event.getTitleByLocale(locale));
        map.put("desc", event.getDescriptionByLocale(locale));
        map.put("confirmed", event.isConfirmed());
        map.put("eventState", event.getEventState());

        map.put("typeByLocale", eventTypeService.getEventTypeValueFromLocale(event.getEventType()));
        map.put("username", event.getUser().getUsername());

        List<Integer> groupSizes = new ArrayList<>();

        List<Event> events = repository.findAll().stream().filter(e -> e.getDate().equals(event.getDate()) && e.getTime().equals(event.getTime())).collect(Collectors.toList());
        events.remove(event);
        events.forEach(e -> groupSizes.add(e.getGroupSize()));
        map.put("groupSizes", groupSizes);

        return map;
    }


    private Boolean isWithinTime(LocalTime timeToCheck, LocalTime startTime, LocalTime endTime) {
        return timeToCheck.isAfter(startTime) && timeToCheck.isBefore(endTime);
    }
}
