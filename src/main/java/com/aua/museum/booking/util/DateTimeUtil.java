package com.aua.museum.booking.util;

import com.aua.museum.booking.domain.Event;
import com.aua.museum.booking.domain.EventLite;
import com.aua.museum.booking.domain.EventType;
import com.aua.museum.booking.domain.Event;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.aua.museum.booking.util.ZonedDateTimeUtil.getArmNowDate;

@Component
public class DateTimeUtil {
    public static final LocalTime WORKDAY_START = LocalTime.of(10, 0);
    public static final LocalTime LAST_POSSIBLE_EVENT_END = LocalTime.of(16, 0);
    public static final int INTERVAL_MINUTES = 15;


    public List<LocalTime> getTimes(List<EventLite> bookedEventsByDate, EventType eventType, LocalDate date, Event... events) {
        if (events.length > 0) {
            Event event = events[0];
            bookedEventsByDate = bookedEventsByDate.stream()
                    .filter(e -> !e.getId().equals(event.getId()))
                    .collect(Collectors.toList());
        }
        Integer duration = eventType.getDuration();
        bookedEventsByDate.sort((o1, o2) -> o1.getTime().isAfter(o2.getTime()) ? 1 : -1);

        LocalTime nowTime = LocalTime.from(ZonedDateTime.now(ZoneId.of("Asia/Yerevan")));
        List<LocalTime> startTimes = new ArrayList<>();
        List<LocalTime> endTimes = new ArrayList<>();
        List<LocalTime> freeTimes = new ArrayList<>();
        bookedEventsByDate.stream()
//                .filter(e -> e.getTime().isAfter(WORKDAY_START))
//                .filter(e -> !e.getDate().equals(getArmNowDate()) || e.getTime().isAfter(nowTime))
                .forEach(e -> {
                    startTimes.add(e.getTime());
                    endTimes.add(e.getTime().plusMinutes(e.getEventType().getDuration()));

                });
        LocalTime currentTime = WORKDAY_START;

//        LocalTime nowTime = LocalTime.now();
        if (date.equals(getArmNowDate())) {
            int hour = nowTime.getHour() + 1;
            int minutes = ((nowTime.getMinute() / 15) * 15 + 15);
            if (minutes >= 60) {
                hour += 1;
                minutes %= 60;
            }
            if (hour < WORKDAY_START.getHour()) {
                hour = 10;
                minutes = 0;
            }

            currentTime = LocalTime.of(hour, minutes);

        }
//        LocalTime finalCurrentTime = currentTime;
        for (int i = 0; i < startTimes.size(); i++) {

            LocalTime startTime = startTimes.get(i);
            LocalTime endTime = endTimes.get(i);

            for (LocalTime timeToCheck = currentTime; !timeToCheck.plusMinutes(duration).isAfter(startTime);
                 timeToCheck = timeToCheck.plusMinutes(INTERVAL_MINUTES)) {
                if (timeToCheck.equals(startTime)) {
                    break;
                }
                freeTimes.add(timeToCheck);
            }
            if (currentTime.isBefore(endTime)) {
                currentTime = endTime;
            }
        }
        if (startTimes.size() == 1) {
            currentTime = endTimes.get(0);
        }
        for (LocalTime timeToCheck = currentTime;
             timeToCheck.plusMinutes(duration).isBefore(LAST_POSSIBLE_EVENT_END.plusMinutes(1));
             timeToCheck = timeToCheck.plusMinutes(INTERVAL_MINUTES)) {
            freeTimes.add(timeToCheck);
        }
        if (events.length > 0) {
            freeTimes.remove(events[0].getTime());
        }
//        if (date.equals(getArmNowDate())) {
//            freeTimes = freeTimes.stream()
//                    .filter(freeTime -> finalCurrentTime.isBefore(freeTime))
//                    .collect(Collectors.toList());
//        }
        return freeTimes;
    }


    public static LocalDateTime getDateTime(LocalDate date, LocalTime time) {
        return LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), time.getHour(), time.getMinute(), 0);
    }
}
