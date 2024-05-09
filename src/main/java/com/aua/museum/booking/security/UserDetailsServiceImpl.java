package com.aua.museum.booking.security;

import com.aua.museum.booking.domain.User;
import com.aua.museum.booking.domain.UserState;
import com.aua.museum.booking.exception.UserAccountIsAlreadyBlockedException;
import com.aua.museum.booking.exception.notfound.UserNotFoundException;
import com.aua.museum.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        User user = optionalUser.orElseThrow(() -> new UserNotFoundException("There isn't registered account with entered username,"));
        if (user.getState() == UserState.BLOCKED) throw new UserAccountIsAlreadyBlockedException();
        return new UserDetailsImpl(user);
    }
}
