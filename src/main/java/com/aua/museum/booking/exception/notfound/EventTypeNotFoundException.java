package com.aua.museum.booking.exception.notfound;

public class EventTypeNotFoundException extends NotFoundException {
    public EventTypeNotFoundException() {
        super("Event type not found!");
    }

    public EventTypeNotFoundException(int eventTypeId) {
        super(String.format("No event type with id %s", eventTypeId));
    }

    public EventTypeNotFoundException(String message) {
        super(message);
    }

}