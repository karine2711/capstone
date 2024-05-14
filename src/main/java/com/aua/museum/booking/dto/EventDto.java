package com.aua.museum.booking.dto;

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
    private String titleAM;
    private String titleRU;
    private String titleEN;
    private String school;
    private String group;

    private Integer groupSize;

    private String descriptionEN;

    private String descriptionRU;

    private String descriptionAM;

    public Integer getEventType() {
        return eventType;
    }

    public LocalTime getTime() {
        return time;
    }

    public Date getDate() {
        return date;
    }

    public String getTitleAM() {
        return titleAM;
    }

    public String getTitleRU() {
        return titleRU;
    }

    public String getTitleEN() {
        return titleEN;
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

    public String getDescriptionEN() {
        return descriptionEN;
    }

    public String getDescriptionRU() {
        return descriptionRU;
    }

    public String getDescriptionAM() {
        return descriptionAM;
    }
}
