package com.aua.museum.booking.service.impl;



import com.aua.museum.booking.domain.Question;
import com.aua.museum.booking.domain.QuestionDetails;
import com.aua.museum.booking.domain.User;
import com.aua.museum.booking.exception.notfound.QuestionNotFoundException;
import com.aua.museum.booking.exception.notfound.UserNotFoundException;
import com.aua.museum.booking.repository.QuestionDetailsRepository;
import com.aua.museum.booking.repository.UserRepository;
import com.aua.museum.booking.service.QuestionDetailsService;
import com.aua.museum.booking.service.QuestionService;
import com.aua.museum.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionDetailsServiceImpl implements QuestionDetailsService {
    private final QuestionDetailsRepository questionDetailsRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final QuestionService questionService;

    @Override
    public List<Question> getQuestionsByEmail(String email) {
        if (!userRepository.existsByEmail(email)) throw new UserNotFoundException(email);
        List<QuestionDetails> details = questionDetailsRepository.getAllByUser_Email(email);
        List<Question> questions = new ArrayList<>();
        details.forEach(detail -> questions.add(detail.getQuestion()));
        return questions;
    }

    @Override
    public boolean checkAnswers(int questionId, String answer, String email) {
        Question question = questionService.getQuestionById(questionId);
        User user = userService.getUserByEmail(email);
        QuestionDetails savedQuestionDetails = user.getQuestionsDetails()
                .stream()
                .filter(questionInList -> questionInList.getQuestion().getDescriptionEN()
                        .equals(question.getDescriptionEN()))
                .findFirst()
                .orElseThrow(() -> new QuestionNotFoundException(questionId));
        return answer.equalsIgnoreCase(savedQuestionDetails.getAnswer());
    }


}
