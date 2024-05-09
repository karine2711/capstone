package com.aua.museum.booking.util;

import com.aua.museum.booking.domain.Event;
import com.aua.museum.booking.domain.EventLite;
import com.aua.museum.booking.domain.EventType;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.aua.museum.booking.util.ZonedDateTimeUtil.getArmNowDate;

@Component
public class DateTimeUtil {
    public static final LocalTime WORKDAY_START = LocalTime.of(10, 0);
    public static final LocalTime WORKDAY_END = LocalTime.of(17, 0);
    public static final int INTERVAL_MINUTES = 15;

    public List<LocalTime> getTimes(List<EventLite> bookedEventsByDate, EventType eventType, LocalDate date) {
        int duration = eventType.getDuration();
        bookedEventsByDate.sort((o1, o2) -> o1.getTime().isAfter(o2.getTime()) ? 1 : -1);
        LocalTime nowTime = LocalTime.from(ZonedDateTime.now(ZoneId.of("Asia/Yerevan")));
        LocalTime timePointer = getStartingTime(date, nowTime);
        List<LocalTime> freeTimes = new ArrayList<>();
        // Get all free times before and between existing events
        for (EventLite eventLite : bookedEventsByDate) {
            LocalTime bookedEventStartTime = eventLite.getTime();
            LocalTime bookedEventEndTime = bookedEventStartTime.plusMinutes(eventLite.getEventType().getDuration());
            while (!timePointer.plusMinutes(duration).isAfter(bookedEventStartTime)) {
//                if (groupsize+lyalac<35 ofdoi)
                freeTimes.add(timePointer);
                timePointer = timePointer.plusMinutes(INTERVAL_MINUTES);
            }
            timePointer = bookedEventEndTime;
        }
        // this is correct with .isBefore as I don't want the events to end sharp when the workday ends
        while (timePointer.plusMinutes(duration).isBefore(WORKDAY_END)){
            freeTimes.add(timePointer);
            timePointer = timePointer.plusMinutes(INTERVAL_MINUTES);
        }
        return freeTimes;
    }

    public List<LocalTime> getTimesForMove(List<EventLite> bookedEventsByDate, EventType eventType, LocalDate date, Event event) {
        bookedEventsByDate = bookedEventsByDate.stream()
                .filter(e -> !e.getId().equals(event.getId()))
                .collect(Collectors.toList());
        List<LocalTime> freeTimes = getTimes(bookedEventsByDate, eventType, date);
        freeTimes.remove(event.getTime());
        return freeTimes;
    }

    public static LocalDateTime getDateTime(LocalDate date, LocalTime time) {
        return LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), time.getHour(), time.getMinute(), 0);
    }

    private static LocalTime getStartingTime(LocalDate date, LocalTime nowTime) {
        LocalTime currentTime = WORKDAY_START;
        // if the user wants to book for today, start free times one hour from now
        if (date.equals(getArmNowDate()) && nowTime.getHour() >= WORKDAY_START.getHour()) {
            int hour = nowTime.getHour() + 1;
            int minutes = ((nowTime.getMinute() / 15) * 15 + 15);
            if (minutes >= 60) {
                hour += 1;
                minutes %= 60;
            }
            currentTime = LocalTime.of(hour, minutes);
        }
        return currentTime;
    }
}
