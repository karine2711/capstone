package com.aua.museum.booking.service;

import com.aua.museum.booking.domain.EventLite;

import java.time.LocalDate;
import java.util.List;

public interface CustomEventService {

    List<EventLite> getAllWithoutPhoto();

    List<EventLite> getActiveUsersPreBookedWithoutPhoto();

    List<EventLite> getBlockedUsersPreBookedWithoutPhoto();

    List<EventLite> getEventsWithTimes(LocalDate localDate);
}
