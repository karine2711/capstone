package com.aua.museum.booking.util;

import java.time.*;

public class ZonedDateTimeUtil {

    public static LocalDate getArmNowDate() {
        return LocalDate.from(ZonedDateTime.now(ZoneId.of("Asia/Yerevan")));
    }

    public static LocalTime getArmNowTime() {
        return LocalTime.from(ZonedDateTime.now(ZoneId.of("Asia/Yerevan")));
    }

    public static LocalDateTime getArmNowDateTime() {
        return LocalDateTime.from(ZonedDateTime.now(ZoneId.of("Asia/Yerevan")));
    }
}
