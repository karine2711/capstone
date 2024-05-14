package com.aua.museum.booking.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Locale;

@Entity
@Table(name = "WEEK_DAY")
@Data
public class WeekDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "display_value_EN", nullable = false, updatable = false)
    private String displayValueEN;

    @Column(name = "display_value_RU", nullable = false, updatable = false)
    private String displayValueRU;

    @Column(name = "display_value_AM", nullable = false, updatable = false)
    private String displayValueAM;

    public String getValueByLocale(Locale locale) {
        switch (locale.getLanguage().toUpperCase()) {
            case "RU":
                return displayValueRU;
            case "EN":
                return displayValueEN;
            default:
                return displayValueAM;
        }
    }
}
