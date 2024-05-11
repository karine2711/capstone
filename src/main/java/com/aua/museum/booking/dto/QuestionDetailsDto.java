package com.aua.museum.booking.dto;

import com.aua.museum.booking.validation.ValidSecurityQuestionAnswer;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class QuestionDetailsDto {

    @NotNull
    private Integer questionId;

    @ValidSecurityQuestionAnswer
    private String answer;

}
