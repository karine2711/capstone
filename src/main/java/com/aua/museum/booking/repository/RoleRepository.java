package com.aua.museum.booking.repository;

import com.aua.museum.booking.domain.Role;
import com.aua.museum.booking.domain.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> getByRoleName(RoleEnum roleEnum);
}
