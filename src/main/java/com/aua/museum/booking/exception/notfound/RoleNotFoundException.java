package com.aua.museum.booking.exception.notfound;

import com.aua.museum.booking.domain.Role;

public class RoleNotFoundException extends NotFoundException {
    public RoleNotFoundException() {
        super("Role not found!");
    }

    public RoleNotFoundException(Role roleName) {
        super(String.format("Role not found: %s", roleName.toString()));
    }
}
