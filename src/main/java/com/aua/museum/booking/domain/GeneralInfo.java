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
    private String titleAM;
    @Column(name = "title_RU", nullable = false)
    private String titleRU;
    @Column(name = "title_EN", nullable = false)
    private String titleEN;
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
    @Column(name = "address_AM", nullable = false)
    private String addressAM;
    @Column(name = "address_RU", nullable = false)
    private String addressRU;
    @Column(name = "address_EN", nullable = false)
    private String addressEN;
    @Builder
    public GeneralInfo(Long id, Timestamp createdDate, Timestamp lastUpdatedDate, String titleAM, String titleRU,
                       String titleEN, WeekDay startWorkingDay, WeekDay endWorkingDay, LocalTime startWorkingTime,
                       LocalTime endWorkingTime, String phone, String email) {
        super(id, createdDate, lastUpdatedDate);
        this.titleAM = titleAM;
        this.titleRU = titleRU;
        this.titleEN = titleEN;
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
                return titleRU;
            case "EN":
                return titleEN;
            default:
                return titleAM;
        }
    }
}
