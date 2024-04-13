package com.aua.museum.booking.mapping;

import com.aua.museum.booking.domain.QuestionDetails;
import com.aua.museum.booking.dto.QuestionDetailsDto;
import com.aua.museum.booking.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QuestionDetailsMapperImpl implements QuestionDetailsMapper {

    private QuestionService questionService;

    @Autowired
    public void setQuestionRepository(QuestionService questionService) {
        this.questionService = questionService;
    }

    @Override
    public QuestionDetails toQuestionDetails(QuestionDetailsDto dto) {
        QuestionDetails questionDetails = new QuestionDetails();
        questionDetails.setQuestion(questionService.getQuestionById(dto.getQuestionId()));
        questionDetails.setAnswer(dto.getAnswer().trim());
        return questionDetails;
    }
}
