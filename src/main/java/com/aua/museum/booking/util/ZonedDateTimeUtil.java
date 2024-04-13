package com.aua.museum.booking.util;

import java.time.*;

public class ZonedDateTimeUtil {

    public static LocalDate getArmNowDate() {
//        LocalTime nowTime=ZonedDateTime.now(ZoneId.of("Asia/Yerevan")).toLocalTime();
//        return ZonedDateTime.now(ZoneId.of("Asia/Yerevan")).toLocalDate();
        return LocalDate.from(ZonedDateTime.now(ZoneId.of("Asia/Yerevan")));
    }

    public static LocalTime getArmNowTime() {
//        return ZonedDateTime.now(ZoneId.of("Asia/Yerevan")).toLocalTime();
        return LocalTime.from(ZonedDateTime.now(ZoneId.of("Asia/Yerevan")));
    }

    public static LocalDateTime getArmNowDateTime() {
//        return ZonedDateTime.now(ZoneId.of("Asia/Yerevan")).toLocalTime();
        return LocalDateTime.from(ZonedDateTime.now(ZoneId.of("Asia/Yerevan")));
    }
}
