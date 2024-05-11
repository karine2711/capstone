package com.aua.museum.booking.domain;

import com.aua.museum.booking.converter.DateConverter;
import com.aua.museum.booking.converter.TimeConverter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "EVENT")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Event extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_type_id")
    private EventType eventType;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "`date`", nullable = false)
    @Convert(converter = DateConverter.class)
    private LocalDate date;

    @DateTimeFormat(pattern = "HH:mm")
    @Column(name = "`time`", nullable = false)
    @Convert(converter = TimeConverter.class)
    private LocalTime time;

    @Column(name = "title_AM")
    private String title_AM;
    @Column(name = "title_RU")
    private String title_RU;
    @Column(name = "title_EN")
    private String title_EN;
    @Column(name = "school", nullable = false)
    private String school;
    @Column(name = "`group`", nullable = false)
    private String group;
    @Column(name = "group_size")
    private Integer groupSize;
    @Column(name = "description_EN", nullable = false)
    private String description_EN;
    @Column(name = "description_RU", nullable = false)
    private String description_RU;
    @Column(name = "description_AM", nullable = false)
    private String description_AM;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "event_state")
    private EventState eventState = EventState.CONFIRMED;
    @JsonIgnore
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private List<Notification> notifications;

    @Builder
    public Event(Long id, Timestamp createdDate, Timestamp lastModifiedDate,
                 User user, EventType eventType, String title_AM, String title_EN, String title_RU, LocalDate date, LocalTime time,
                 String school, String group, Integer groupSize, String description_AM, String description_RU, String description_EN) {
        super(id, createdDate, lastModifiedDate);
        this.user = user;
        this.eventType = eventType;
        this.date = date;
        this.time = time;
        this.school = school;
        this.group = group;
        this.groupSize = groupSize;
        this.title_AM = title_AM;
        this.title_RU = title_RU;
        this.title_EN = title_EN;
        this.description_AM = description_AM;
        this.description_EN = description_EN;
        this.description_RU = description_RU;
    }

    public Event() {
    }

    public String getDescriptionByLocale(Locale locale) {
        return switch (locale.getLanguage().toUpperCase()) {
            case "RU" -> description_RU;
            case "EN" -> description_EN;
            default -> description_AM;
        };
    }

    public String getTitleByLocale(Locale locale) {
        return switch (locale.getLanguage().toUpperCase()) {
            case "RU" -> title_RU;
            case "EN" -> title_EN;
            default -> title_AM;
        };
    }

    public boolean isWithin24Hours() {
        LocalDate nowDate = LocalDate.from(ZonedDateTime.now(ZoneId.of("Asia/Yerevan")));
        LocalTime nowTime = LocalTime.from(ZonedDateTime.now(ZoneId.of("Asia/Yerevan")));
        return nowDate.isEqual(date) || (nowDate.plusDays(1).equals(date) && nowTime.isAfter(time));
    }

    public boolean isConfirmed() {
        return eventState.equals(EventState.CONFIRMED);
    }

    public void setConfirmed() {
        eventState = EventState.CONFIRMED;
    }
}
