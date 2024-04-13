package com.aua.museum.booking.exception.notfound;

public class EventNotFoundException extends NotFoundException {
    public EventNotFoundException() {
        super();
    }

    public EventNotFoundException(long id) {
        super(String.format("Event with id %d not found!", id));
    }

    public EventNotFoundException(String info) {
        super(info);
    }
}
