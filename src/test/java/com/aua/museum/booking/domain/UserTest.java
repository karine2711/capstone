package com.aua.museum.booking.domain;import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.assertEquals;class UserTest {    User user = new User();    @Test    void addRole() {        user.setRole(Role.USER_ROLE);        assertEquals(Role.USER_ROLE, user.getRole());        user.setRole(Role.ADMIN_ROLE);        assertEquals(Role.ADMIN_ROLE, user.getRole());    }}