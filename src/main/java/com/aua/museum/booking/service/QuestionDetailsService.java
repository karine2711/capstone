package com.aua.museum.booking.service;

import com.aua.museum.booking.domain.Question;

import java.util.List;

public interface QuestionDetailsService {

    List<Question> getQuestionsByEmail(String email);

    boolean checkAnswers(int questionId, String answer, String email);
}
