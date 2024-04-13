package com.aua.museum.booking.dto;

import com.aua.museum.booking.validation.ValidCellPhone;
import com.aua.museum.booking.validation.ValidEmail;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;



import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class GeneralInfoDto {

    @NotNull
    private String title_AM;
    @NotNull
    private String title_RU;
    @NotNull
    private String title_EN;

    @NotNull
    private Long startWorkingDayId;

    @NotNull
    private Long endWorkingDayId;

    @DateTimeFormat(pattern = "HH:mm")
    @NotNull
    private LocalTime startWorkingTime;

    @DateTimeFormat(pattern = "HH:mm")
    @NotNull
    private LocalTime endWorkingTime;


    @NotNull(message = "not.null.email")
    @ValidEmail
    private String email;

    @NotNull(message = "not.null.phone")
    @ValidCellPhone
    private String phone;

}
