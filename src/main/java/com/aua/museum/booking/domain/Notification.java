package com.aua.museum.booking.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import jakarta.persistence.*;
import java.util.Locale;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notification")
public class Notification extends BaseEntity {

    @Column(name = "title_am")
    private String titleAm;

    @Column(name = "title_en")
    private String titleEn;

    @Column(name = "title_ru")
    private String titleRu;

    @Column(name = "body_am")
    @EqualsAndHashCode.Exclude
    private String bodyAm;

    @Column(name = "body_en")
    @EqualsAndHashCode.Exclude
    private String bodyEn;

    @Column(name = "body_ru")
    @EqualsAndHashCode.Exclude
    private String bodyRu;

    @ManyToOne
    @JoinColumn(name = "user_id")
//    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id")
//    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Event event;

    @Column(name = "shown")
    @EqualsAndHashCode.Exclude
    private boolean isShown;

    @Column(name = "seen")
    @EqualsAndHashCode.Exclude
    private boolean isSeen;

    public String getBodyByLocale(Locale locale) {
        switch (locale.getLanguage().toUpperCase()) {
            case "RU":
                return bodyRu;
            case "EN":
                return bodyEn;
            default:
                return bodyAm;
        }
    }

    public String getTitleByLocale(Locale locale) {
        switch (locale.getLanguage().toUpperCase()) {
            case "RU":
                return titleRu;
            case "EN":
                return titleEn;
            default:
                return titleAm;
        }
    }
}
