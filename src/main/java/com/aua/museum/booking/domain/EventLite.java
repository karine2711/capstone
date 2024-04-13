package com.aua.museum.booking.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class EventLite {

    private Long id;
    private User user;
    private EventType eventType;
    private String title_AM;
    private String title_EN;
    private String title_RU;
    private LocalDate date;
    private LocalTime time;
    private String school;
    private String group;
    private Integer groupSize;
    private String description_AM;
    private String description_RU;
    private String description_EN;
    private EventState eventState;

    public EventLite(Long id, EventType eventType, LocalDate date, LocalTime time, Integer groupSize) {
        this.id = id;
        this.eventType = eventType;
        this.date = date;
        this.time = time;
        this.groupSize = groupSize;
    }

    public String getDescriptionByLocale(Locale locale) {
        switch (locale.getLanguage().toUpperCase()) {
            case "RU":
                return description_RU;
            case "HY":
                return description_AM;
            default:
                return description_EN;
        }

    }

    public String getTitleByLocale(Locale locale) {
        switch (locale.getLanguage().toUpperCase()) {
            case "RU":
                return title_RU;
            case "HY":
                return title_AM;
            default:
                return title_EN;
        }
    }

    public boolean isConfirmed() {
        return eventState == EventState.CONFIRMED;
    }
}
