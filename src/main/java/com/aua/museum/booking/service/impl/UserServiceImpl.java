package com.aua.museum.booking.service.impl;



import com.aua.museum.booking.domain.Role;
import com.aua.museum.booking.domain.User;
import com.aua.museum.booking.domain.UserState;
import com.aua.museum.booking.exception.UserAccountIsAlreadyBlockedException;
import com.aua.museum.booking.exception.notfound.UserNotFoundException;
import com.aua.museum.booking.exception.notunique.FieldsAlreadyExistException;
import com.aua.museum.booking.repository.UserRepository;
import com.aua.museum.booking.security.UserDetailsImpl;
import com.aua.museum.booking.service.EditProfileService;
import com.aua.museum.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private static final String BLOCKED_KEYWORD = "_blocked_";
    private final UserRepository repository;
     @Qualifier("sessionRegistry")
    private final SessionRegistry sessionRegistry;
    private PasswordEncoder encoder;
    private EditProfileService editProfileService;

    @Autowired
    public void setEncoder(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Autowired
    public void setEditProfileService(EditProfileService editProfileService) {
        this.editProfileService = editProfileService;
    }

    @Override
    public User createUser(User user) {
        boolean usernameExists = repository.existsByUsername(user.getUsername());
        boolean emailExists = repository.existsByEmail(user.getEmail());
        if (usernameExists || emailExists) {
            Map<String, Boolean> fieldErrors = new HashMap<>();
            fieldErrors.put("username", usernameExists);
            fieldErrors.put("email", emailExists);
            throw new FieldsAlreadyExistException(fieldErrors);
        }
        user.setPassword(encoder.encode(user.getPassword()));
        user.getQuestionsDetails().forEach(questionDetails -> questionDetails.setUser(user));
        return repository.save(user);
    }

    @Override
    public User getUserById(long id) {
        return repository.findById(id).orElseThrow(() ->
                new UserNotFoundException(id));
    }

    @Override
    public User getUserByUsername(String username) {
        return repository.findByUsername(username).orElseThrow(() ->
                new UserNotFoundException(username));
    }

    @Override
    public User getUserByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException(email));
    }

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public List<User> getAllAdmins() {
        return repository.findAll().stream().filter(user -> user.isAdmin() && !user.isSuperAdmin()).collect(Collectors.toList());
    }

    @Override
    public User blockUserByEmail(String email) {
        Optional<User> optionalUser = repository.findByEmail(email);
        User user = optionalUser.orElseGet(() -> findByBlocked(email));
        if (user.getState() == UserState.BLOCKED) {
            throw new UserAccountIsAlreadyBlockedException(email);
        }
        doBlockUser(user, email);
        return repository.save(user);
    }

    @Override
    public User unblockUserById(long id) {
        User user = repository.findById(id).orElseThrow(UserNotFoundException::new);

        doUnblockUser(user, user.getEmail());

        return repository.save(user);
    }

    private User findByBlocked(String email) {
        String blockedEmail = BLOCKED_KEYWORD + email;
        return repository.findByEmailEndingWith(blockedEmail)
                .orElseThrow(UserNotFoundException::new);
    }

    private String getBlockedEmail(Long id, String email) {
        return id + BLOCKED_KEYWORD + email;
    }

    private String getUnblockedEmail(Long id, String email) {
        return email.substring((id + BLOCKED_KEYWORD).length());
    }

    private void doBlockUser(User user, String email) {
        String blockedEmail = getBlockedEmail(user.getId(), email);
        user.setEmail(blockedEmail);
        user.setState(UserState.BLOCKED);
    }

    private void doUnblockUser(User user, String email) {
        String unblockedEmail = getUnblockedEmail(user.getId(), email);
        user.setEmail(unblockedEmail);
        user.setState(UserState.UNBLOCKED);
    }

    @Override
    public User changePassword(String email, String newPassword) {
        User user = getUserByEmail(email);
        user.setPassword(encoder.encode(newPassword));
        return repository.save(user);
    }

    @Override
    public String extractAvatarPicture(User user) {
        if (user.getProfileAvatar() == null) return null;
        byte[] encode = Base64.getEncoder().encode(user.getProfileAvatar());
        return new String(encode, StandardCharsets.UTF_8);
    }


    @Override
    public void updateUser(User user, MultipartHttpServletRequest request) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Optional<User> possibleUser = repository.findByUsername(user.getUsername());
        if (possibleUser.isPresent()) {
            User user1 = possibleUser.get();
            user1.setFullName(user.getFullName());
            user1.setPhone(user.getPhone());
            user1.setAddress(user.getAddress());
            user1.setOccupation(user.getOccupation());
            if (user.getPassword().length() != 0) user1.setPassword(encoder.encode(user.getPassword()));
            user1.setSchool(user.getSchool());
            user1.setResidency(user.getResidency());
            if (user.getProfileAvatar() != null) user1.setProfileAvatar(user.getProfileAvatar());
            else if (request.getParameter("deletedAvatar").equals("deleted")) {
                user1.setProfileAvatar(null);
            }
            user1.getQuestionsDetails().get(0).setQuestion(user.getQuestionsDetails().get(0).getQuestion());
            user1.getQuestionsDetails().get(0).setAnswer(user.getQuestionsDetails().get(0).getAnswer());
            user1.getQuestionsDetails().get(1).setQuestion(user.getQuestionsDetails().get(1).getQuestion());
            user1.getQuestionsDetails().get(1).setAnswer(user.getQuestionsDetails().get(1).getAnswer());
            repository.save(user1);
        }
    }

    @Override
    public void deleteUserByUsername(String userName) {
        repository.deleteByUsername(userName);
    }

    @Override
    public User save(User user) {
        return repository.save(user);
    }


    @Override
    public void toActive(Long id) {
        User user = repository.findById(id).orElseThrow(UserNotFoundException::new);
        user.setState(UserState.ACTIVE);
        repository.save(user);
    }

    @Override
    public void toUser(Long id) {
        User user = getUserById(id);
        user.setRole(Role.USER_ROLE);
        logoutUser(id);
    }

    @Override
    public void toAdmin(Long id) {
        User user = getUserById(id);
        user.setRole(Role.ADMIN_ROLE);
        logoutUser(id);
    }


    public void logoutUser(Long id) {
        User user = getUserById(id);
        UserDetails userD = new UserDetailsImpl(user);

        List<Object> principals = sessionRegistry.getAllPrincipals();

        for (Object principal : principals) {
            if (principal.equals(userD)) {
                List<SessionInformation> sessionInformations = sessionRegistry.getAllSessions(principal, false);
                for (SessionInformation sessionInformation : sessionInformations) {
                    sessionInformation.expireNow();
                }
            }
        }

    }

    @Override
    @Scheduled(cron = "0 0 5 * * *")
    public void deleteUnblockedUserAfterWeek() {
        List<User> deletableUsers = repository.findByState(UserState.UNBLOCKED).stream()
                .filter(u -> u.getLastUpdatedDate().toLocalDateTime().plusDays(7)
                        .isBefore(LocalDateTime.now())).collect(Collectors.toList());
        for (User u : deletableUsers) {
            repository.delete(u);
        }
    }
}
