package com.aua.museum.booking.exception.notfound;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(long id) {
        super(String.format("User with id %d not found!", id));
    }

    public UserNotFoundException(String info) {
        super(info);
    }
}
