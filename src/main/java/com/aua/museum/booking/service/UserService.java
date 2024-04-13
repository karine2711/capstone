package com.aua.museum.booking.service;

import com.aua.museum.booking.domain.User;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User getUserById(long id);

    User getUserByUsername(String username);

    User getUserByEmail(String email);

    List<User> getAllUsers();

    List<User> getAllAdmins();

    User blockUserByEmail(String email);

    User unblockUserById(long id);

    User changePassword(String email, String newPassword);

    String extractAvatarPicture(User user);

    void updateUser(User user, MultipartHttpServletRequest request);

    void deleteUserByUsername(String userName);

    User save(User user);

    void toActive(Long id);

    void toUser(Long id);

    void toAdmin(Long id);

    void deleteUnblockedUserAfterWeek();
}
