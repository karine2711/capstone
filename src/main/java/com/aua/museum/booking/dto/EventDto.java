package com.aua.museum.booking.dto;

import com.aua.museum.booking.validation.ValidEventTime;
import com.aua.museum.booking.validation.ValidEventTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {

    Integer eventType;

    @NotNull
    @ValidEventTime
    private LocalTime time;
    @NotNull
    private Date date;
    private String title_AM;
    private String title_RU;
    private String title_EN;
    private String school;
    private String group;

    private Integer groupSize;

    private String description_EN;

    private String description_RU;

    private String description_AM;

    public Integer getEventType() {
        return eventType;
    }

    public LocalTime getTime() {
        return time;
    }

    public Date getDate() {
        return date;
    }

    public String getTitle_AM() {
        return title_AM;
    }

    public String getTitle_RU() {
        return title_RU;
    }

    public String getTitle_EN() {
        return title_EN;
    }

    public String getSchool() {
        return school;
    }

    public String getGroup() {
        return group;
    }

    public Integer getGroupSize() {
        return groupSize;
    }

    public String getDescription_EN() {
        return description_EN;
    }

    public String getDescription_RU() {
        return description_RU;
    }

    public String getDescription_AM() {
        return description_AM;
    }
}
