package com.aua.museum.booking.mapping;

import com.aua.museum.booking.domain.QuestionDetails;
import com.aua.museum.booking.dto.QuestionDetailsDto;

public interface QuestionDetailsMapper {

    QuestionDetails toQuestionDetails(QuestionDetailsDto dto);

}
