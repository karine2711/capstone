package com.aua.museum.booking.service.impl;import com.aua.museum.booking.exception.notfound.QuestionNotFoundException;import com.aua.museum.booking.repository.QuestionRepository;import com.aua.museum.booking.repository.UserRepository;import org.junit.jupiter.api.Test;import org.junit.jupiter.api.extension.ExtendWith;import org.mockito.InjectMocks;import org.mockito.Mock;import org.mockito.junit.jupiter.MockitoExtension;import java.util.Optional;import static org.junit.jupiter.api.Assertions.assertThrows;import static org.mockito.BDDMockito.given;@ExtendWith(MockitoExtension.class)class QuestionServiceImplTest {    @InjectMocks    private static QuestionServiceImpl questionService;    @Mock    private QuestionRepository questionRepository;    @Mock    private UserRepository userRepository;    @Test    void getQuestionById() {        given(questionRepository.findById(1)).willReturn(Optional.empty());        assertThrows(QuestionNotFoundException.class, () -> questionService.getQuestionById(1));    }}