package com.aua.museum.booking.mapping;

import com.aua.museum.booking.domain.QuestionDetails;
import com.aua.museum.booking.domain.Role;
import com.aua.museum.booking.domain.RoleEnum;
import com.aua.museum.booking.domain.User;
import com.aua.museum.booking.dto.UserDto;
import com.aua.museum.booking.service.RoleService;
import com.aua.museum.booking.domain.QuestionDetails;
import com.aua.museum.booking.domain.Role;
import com.aua.museum.booking.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public abstract class UserMapperDecorator implements UserMapper {
    @Autowired
    RoleService roleService;
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

        Role role = roleService.getRole(RoleEnum.USER_ROLE);
        List<QuestionDetails> details = new ArrayList<>();

        user.addRole(role);
        user.setProfileAvatar(dto.getProfileAvatar());

        if (dto.getQuestionsDetails() != null) {
            dto.getQuestionsDetails().forEach(questionDetailsDto
                    -> details.add(questionDetailsMapper.toQuestionDetails(questionDetailsDto)));
            user.setQuestionsDetails(details);
        }

        return user;
    }
}
