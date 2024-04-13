package com.aua.museum.booking.exception;

public class UserAccountIsAlreadyBlockedException extends RuntimeException {

    public UserAccountIsAlreadyBlockedException() {
        super("Account is blocked!");
    }

    public UserAccountIsAlreadyBlockedException(String info) {
        super(String.format("Account '%s' is already blocked!", info));
    }
}
