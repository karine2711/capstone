package com.aua.museum.booking.repository;

import com.aua.museum.booking.domain.User;
import com.aua.museum.booking.domain.UserState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByState(UserState userState);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Override
    <S extends User> S save(S s);

    Optional<User> findByEmailEndingWith(String email);

    void deleteByUsername(String userName);
}
