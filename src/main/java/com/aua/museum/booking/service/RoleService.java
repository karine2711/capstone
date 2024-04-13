package com.aua.museum.booking.service;

import com.aua.museum.booking.domain.Role;
import com.aua.museum.booking.domain.RoleEnum;

public interface RoleService {
    Role getRole(RoleEnum roleName);
}
