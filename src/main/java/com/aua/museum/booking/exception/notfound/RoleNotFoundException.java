package com.aua.museum.booking.exception.notfound;

import com.aua.museum.booking.domain.RoleEnum;

public class RoleNotFoundException extends NotFoundException {
    public RoleNotFoundException() {
        super("Role not found!");
    }

    public RoleNotFoundException(RoleEnum roleName) {
        super(String.format("Role not found: %s", roleName.toString()));
    }
}
