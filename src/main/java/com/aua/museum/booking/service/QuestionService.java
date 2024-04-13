package com.aua.museum.booking.service;

import com.aua.museum.booking.domain.Question;

import java.util.List;
import java.util.Map;


public interface QuestionService {

    Question getQuestionById(int id);

    List<Question> getAllQuestions();

    String getQuestionFromLocale(Question question);

    Map<String, Object> getLocalizedQuestionAsMap(Question question);
}
