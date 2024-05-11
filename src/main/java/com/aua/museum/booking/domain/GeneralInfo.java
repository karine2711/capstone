package com.aua.museum.booking.domain;


import com.aua.museum.booking.converter.TimeConverter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.Locale;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "MUSEUM_INFO")
@Data
@NoArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class GeneralInfo extends BaseEntity {

    @Column(name = "title_AM", nullable = false)
    private String title_AM;
    @Column(name = "title_RU", nullable = false)
    private String title_RU;
    @Column(name = "title_EN", nullable = false)
    private String title_EN;
    @OneToOne
    @JoinColumn(name = "start_working_day_id", referencedColumnName = "id")
    private WeekDay startWorkingDay;
    @OneToOne
    @JoinColumn(name = "end_working_day_id", referencedColumnName = "id")
    private WeekDay endWorkingDay;
    @DateTimeFormat(pattern = "HH:mm")
    @Column(name = "start_working_day_time", nullable = false)
    @Convert(converter = TimeConverter.class)
    private LocalTime startWorkingTime;
    @DateTimeFormat(pattern = "HH:mm")
    @Column(name = "end_working_day_time", nullable = false)
    @Convert(converter = TimeConverter.class)
    private LocalTime endWorkingTime;
    @Column(name = "phone", nullable = false)
    private String phone;
    @Column(name = "email", nullable = false)
    private String email;

    @Builder
    public GeneralInfo(Long id, Timestamp createdDate, Timestamp lastUpdatedDate, String title_AM, String title_RU,
                       String title_EN, WeekDay startWorkingDay, WeekDay endWorkingDay, LocalTime startWorkingTime,
                       LocalTime endWorkingTime, String phone, String email) {
        super(id, createdDate, lastUpdatedDate);
        this.title_AM = title_AM;
        this.title_RU = title_RU;
        this.title_EN = title_EN;
        this.startWorkingDay = startWorkingDay;
        this.endWorkingDay = endWorkingDay;
        this.startWorkingTime = startWorkingTime;
        this.endWorkingTime = endWorkingTime;
        this.phone = phone;
        this.email = email;
    }

    public String getTitleByLocale(Locale locale) {
        switch (locale.getLanguage().toUpperCase()) {
            case "RU":
                return title_RU;
            case "EN":
                return title_EN;
            default:
                return title_AM;
        }
    }
}
