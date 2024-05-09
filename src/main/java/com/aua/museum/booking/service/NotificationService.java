package com.aua.museum.booking.service;

import com.aua.museum.booking.domain.Event;
import com.aua.museum.booking.domain.Notification;
import com.aua.museum.booking.domain.User;


import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Set;

public interface NotificationService {

    List<Notification> getUsersAllNotifications(User user);

    Notification createReminder(User user, Event event);

    Notification createConfirm(User user, Event event);

    Notification createReminderAboutConfirm(User user, Event event);

    Notification createReschedule(User user, Event event);

    Notification createPreBookedUser(User user, Event event);

    Notification createPreBookedAdmin(User user, Event event);

    Notification createPreBookedActivityConfirmed(User user, Event event);

    Notification createPreBookedActivityRescheduled(User user, Event event);

    Notification createPreBookedActivityDeleted(User user, Event event);

    Notification save(Notification notification);

    @Transactional
    void saveNotifications(Set<Long> ids);

    String getLocalizedTitle(Notification notification);

    String getLocalizedBody(Notification notification);

    void setUsersAllNotificationsShownAndSeen(User user);

    List<Notification> findByIdCreatedDateDesc(long id);

    List<Notification> findNotSeenOrNotShownByUserId(long id);

    void delete(Notification notification);

    void findAndDeleteDuplicateOfNotification(Notification notification, User user);
}
