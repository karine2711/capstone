package com.aua.museum.booking.mapping;

import com.aua.museum.booking.domain.QuestionDetails;
import com.aua.museum.booking.domain.Role;
import com.aua.museum.booking.domain.User;
import com.aua.museum.booking.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public abstract class UserMapperDecorator implements UserMapper {
    private UserMapper userMapper;

    private QuestionDetailsMapper questionDetailsMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Autowired
    public void setQuestionDetailsMapper(QuestionDetailsMapper questionDetailsMapper) {
        this.questionDetailsMapper = questionDetailsMapper;
    }


    @Override
    public User toEntity(UserDto dto) {
        User user = userMapper.toEntity(dto);

        List<QuestionDetails> details = new ArrayList<>();

        user.setRole(Role.USER_ROLE);
        user.setProfileAvatar(dto.getProfileAvatar());

        if (dto.getQuestionsDetails() != null) {
            dto.getQuestionsDetails().forEach(questionDetailsDto
                    -> details.add(questionDetailsMapper.toQuestionDetails(questionDetailsDto)));
            user.setQuestionsDetails(details);
        }

        return user;
    }
}
