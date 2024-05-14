package com.aua.museum.booking.util;

import java.time.*;

public class ZonedDateTimeUtil {

    private ZonedDateTimeUtil() {
    }

    public static final String ASIA_YEREVAN = "Asia/Yerevan";

    public static LocalDate getArmNowDate() {
        return LocalDate.from(ZonedDateTime.now(ZoneId.of(ASIA_YEREVAN)));
    }

    public static LocalTime getArmNowTime() {
        return LocalTime.from(ZonedDateTime.now(ZoneId.of(ASIA_YEREVAN)));
    }

    public static LocalDateTime getArmNowDateTime() {
        return LocalDateTime.from(ZonedDateTime.now(ZoneId.of(ASIA_YEREVAN)));
    }
}
