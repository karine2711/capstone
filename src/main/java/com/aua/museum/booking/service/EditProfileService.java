package com.aua.museum.booking.service;

import com.aua.museum.booking.domain.User;
import com.aua.museum.booking.dto.UserDto;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

public interface EditProfileService {
    ModelAndView getModelWithUserAttributes(User user);

    UserDto extractUserDtoFromRequest(MultipartHttpServletRequest request);

    void addUserToQuestionDetails(User user);
}
