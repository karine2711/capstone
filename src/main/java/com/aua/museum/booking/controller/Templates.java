package com.aua.museum.booking.controller;

public enum Templates {
    FORGOT_PASSWORD("forgot-password"),
    LANDING("landing"),
    LOGIN("login"),
    QUESTIONS("questions"),
    REGISTRATION("registration"),
    RESET_PASSWORD("reset-password"),
    EDIT_PROFILE("edit-profile"),
    HOMEPAGE("calendar"),
    HOMEPAGE_ADMIN("calendarAdmin"),
    ADD_ACTIVITY("activity"),
    USER_LIST("user-list"),
    NOTIFICATION_CENTER("notification-center"),
    WAITING_LIST("waiting-list"),
    EVENT_LIST("event-list");

    private final String templateName;

    Templates(String templateName) {
        this.templateName = templateName;
    }

    public String getName() {
        return templateName;
    }
}
