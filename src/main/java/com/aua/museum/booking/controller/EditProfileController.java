package com.aua.museum.booking.controller;

import com.aua.museum.booking.domain.User;
import com.aua.museum.booking.dto.UserDto;
import com.aua.museum.booking.mapping.UserMapperDecorator;
import com.aua.museum.booking.service.EditProfileService;
import com.aua.museum.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Principal;

@Controller
@RequestMapping("/edit-profile")
@RequiredArgsConstructor
public class EditProfileController {

    private final UserService userService;
    private final EditProfileService editProfileService;
    private final UserMapperDecorator mapper;

    @GetMapping
    public ModelAndView getEditProfilePage(Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        return editProfileService.getModelWithUserAttributes(user);
    }

    @PostMapping(headers = ("content-type=multipart/form-data"))
    public @ResponseBody
    ResponseEntity<Object> updateUser(MultipartHttpServletRequest request) {
        UserDto userDto = editProfileService.ExtractUserDtoFromRequest(request);
        User user = mapper.toEntity(userDto);
        editProfileService.addUserToQuestionDetails(user);
        userService.updateUser(user, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteUser(HttpServletRequest request, HttpServletResponse response, Principal principal) throws ServletException {
        userService.deleteUserByUsername(principal.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/avatar/{username}")
    public @ResponseBody
    ResponseEntity<Object> getUserPhoto(@PathVariable String username) {
        final User user = userService.getUserByUsername(username);
        final String avatar = userService.extractAvatarPicture(user);
        return avatar == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(avatar);
    }
}