package com.aua.museum.booking.service.impl;



import com.aua.museum.booking.controller.Templates;
import com.aua.museum.booking.domain.User;
import com.aua.museum.booking.dto.QuestionDetailsDto;
import com.aua.museum.booking.dto.UserDto;
import com.aua.museum.booking.service.EditProfileService;
import com.aua.museum.booking.service.QuestionService;
import com.aua.museum.booking.service.UserService;
import com.aua.museum.booking.util.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class EditProfileServiceImpl implements EditProfileService {

    private final UserService userService;
    private final QuestionService questionService;

    @Override
    public ModelAndView getModelWithUserAttributes(User user) {
        ModelAndView model = new ModelAndView(Templates.EDIT_PROFILE.getName());

        model.addObject("profileAvatar", userService.extractAvatarPicture(user));
        model.addObject("user", user);
        model.addObject("questions", getSecurityQuestions());

        return model;
    }

    @Override
    public UserDto extractUserDtoFromRequest(MultipartHttpServletRequest request) {
        UserDto userDto = new UserDto();
        userDto.setFullName(request.getParameter("fullName"));
        userDto.setEmail(request.getParameter("email"));
        userDto.setUsername(request.getParameter("userName"));
        userDto.setPhone(request.getParameter("phone"));
        userDto.setPassword(request.getParameter("password"));
        userDto.setConfirmPassword(request.getParameter("confirmPassword"));
        userDto.setSchool(request.getParameter("school"));
        userDto.setOccupation(request.getParameter("occupation"));
        userDto.setResidency(request.getParameter("residency"));
        userDto.setAddress(request.getParameter("address"));
        userDto.setQuestionsDetails(addQuestionDetails(request));
        try {
            MultipartFile profileFile = request.getFile("profileAvatar");
            assert profileFile != null;
            if (profileFile.getBytes().length != 0)
                userDto.setProfileAvatar(ImageService.compress(ImageService.extractImageFromFile(profileFile)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userDto;
    }

    private Map<Integer, String> getSecurityQuestions() {
        Map<Integer, String> questions = new HashMap<>();
        questionService.getAllQuestions()
                .forEach(question -> questions.put(question.getId(), questionService.getQuestionFromLocale(question)));
        return questions;
    }

    private ArrayList<QuestionDetailsDto> addQuestionDetails(MultipartHttpServletRequest request) {
        QuestionDetailsDto firstQuestionDetails = new QuestionDetailsDto();
        QuestionDetailsDto secondQuestionDetails = new QuestionDetailsDto();

        firstQuestionDetails.setQuestionId(Integer.valueOf(request.getParameter("first-question")));
        firstQuestionDetails.setAnswer(request.getParameter("firstAnswer"));

        secondQuestionDetails.setQuestionId(Integer.valueOf(request.getParameter("second-question")));
        secondQuestionDetails.setAnswer(request.getParameter("secondAnswer"));

        return new ArrayList<>(List.of(firstQuestionDetails, secondQuestionDetails));
    }

    public void addUserToQuestionDetails(User user) {
        user.getQuestionsDetails().forEach(questionDetails -> questionDetails.setUser(user));
    }
}
