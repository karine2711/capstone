package com.aua.museum.booking.service.impl;



import com.aua.museum.booking.domain.Question;
import com.aua.museum.booking.exception.notfound.QuestionNotFoundException;
import com.aua.museum.booking.locale.Language;
import com.aua.museum.booking.repository.QuestionRepository;
import com.aua.museum.booking.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;

    @Override
    public Question getQuestionById(int id) {
        return questionRepository.findById(id).orElseThrow(() -> new QuestionNotFoundException(id));
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public String getQuestionFromLocale(Question question) {
        final Locale locale = LocaleContextHolder.getLocale();
        final Language language = Language.valueOf(locale.getLanguage().toUpperCase());
        return switch (language) {
            case EN -> question.getDescription_EN();
            case RU -> question.getDescription_RU();
            default -> question.getDescription_AM();
        };
    }

    @Override
    public Map<String, Object> getLocalizedQuestionAsMap(Question question) {
        Map<String, Object> questionMap = new HashMap<>();
        questionMap.put("id", question.getId());
        questionMap.put("description", getQuestionFromLocale(question));
        return questionMap;
    }

}
