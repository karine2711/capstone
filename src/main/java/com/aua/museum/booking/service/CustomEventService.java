package com.aua.museum.booking.service;

import com.aua.museum.booking.domain.EventLite;

import java.util.List;

public interface CustomEventService {

    List<EventLite> getAllWithoutPhoto();

    List<EventLite> getActiveUsersPreBookedWithoutPhoto();

    List<EventLite> getBlockedUsersPreBookedWithoutPhoto();

}
