package com.aua.museum.booking.dto;

import com.aua.museum.booking.validation.*;
import com.aua.museum.booking.validation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.PropertySource;

import jakarta.persistence.Lob;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MatchingPassword
@PropertySource("classpath:ValidationMessages.properties")
public class UserDto {

    @NotNull(message = "not.null.username")
    @ValidUsername
    private String username;

    @NotNull(message = "not.null.password")
    @ValidPassword
    private String password;

    @NotNull(message = "not.null.confirm.password")
    private String confirmPassword;

    @NotNull(message = "not.null.full.name")
    @ValidFullName
    private String fullName;

    @NotNull(message = "not.null.email")
    @ValidEmail
    private String email;

    @NotNull(message = "not.null.phone")
    @ValidCellPhone
    private String phone;

    private String school;

    private String occupation;

    private String residency;

    private String address;

    @Lob
    private byte[] profileAvatar;

    @NotNull
    private List<@Valid QuestionDetailsDto> questionsDetails;

}

