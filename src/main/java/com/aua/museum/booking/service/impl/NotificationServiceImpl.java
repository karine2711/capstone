package com.aua.museum.booking.service.impl;


import com.aua.museum.booking.domain.Event;
import com.aua.museum.booking.domain.Notification;
import com.aua.museum.booking.domain.User;
import com.aua.museum.booking.domain.UserState;
import com.aua.museum.booking.locale.Language;
import com.aua.museum.booking.repository.EventRepository;
import com.aua.museum.booking.repository.NotificationRepository;
import com.aua.museum.booking.repository.UserRepository;
import com.aua.museum.booking.service.NotificationService;
import com.aua.museum.booking.service.notifications.FCMService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.aua.museum.booking.util.ZonedDateTimeUtil.getArmNowDate;
import static com.aua.museum.booking.util.ZonedDateTimeUtil.getArmNowTime;

@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {

    private final MessageSource messageSource;
    private final NotificationRepository notificationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final FCMService messagingService;

    @Override
    public List<Notification> getUsersAllNotifications(User user) {
        return notificationRepository.findByUser_Id(user.getId());
    }

    @Override
    public Notification createReminder(User user, Event event) {
        final Object[] timeArg = {timeToString(event.getTime())};
        return Notification.builder()
                .titleAm(messageSource.getMessage("notification.reminder.title", new Object[]{}, new Locale("hy")))
                .titleEn(messageSource.getMessage("notification.reminder.title", new Object[]{}, Locale.US))
                .titleRu(messageSource.getMessage("notification.reminder.title", new Object[]{}, new Locale("ru")))
                .bodyAm(messageSource.getMessage("notification.reminder.body", timeArg, new Locale("hy")))
                .bodyEn(messageSource.getMessage("notification.reminder.body", timeArg, Locale.US))
                .bodyRu(messageSource.getMessage("notification.reminder.body", timeArg, new Locale("ru")))
                .user(user)
                .event(event)
                .isShown(false)
                .build();
    }

    @Override
    public Notification createConfirm(User user, Event event) {
        return Notification.builder()
                .titleAm(messageSource.getMessage("notification.confirm.title", new Object[]{}, new Locale("hy")))
                .titleEn(messageSource.getMessage("notification.confirm.title", new Object[]{}, Locale.US))
                .titleRu(messageSource.getMessage("notification.confirm.title", new Object[]{}, new Locale("ru")))
                .user(user)
                .event(event)
                .isShown(false)
                .build();
    }

    @Override
    public Notification createReminderAboutConfirm(User user, Event event) {
        return Notification.builder()
                .titleAm(messageSource.getMessage("notification.confirm.description.title", new Object[]{}, new Locale("hy")))
                .titleEn(messageSource.getMessage("notification.confirm.description.title", new Object[]{}, Locale.US))
                .titleRu(messageSource.getMessage("notification.confirm.description.title", new Object[]{}, new Locale("ru")))
                .user(user)
                .event(event)
                .isShown(false)
                .build();
    }

    @Override
    public Notification createPreBookedUser(User user, Event event) {
        return Notification.builder()
                .titleAm(messageSource.getMessage("notification.preBooked.user.title", new Object[]{}, new Locale("hy")))
                .titleEn(messageSource.getMessage("notification.preBooked.user.title", new Object[]{}, Locale.US))
                .titleRu(messageSource.getMessage("notification.preBooked.user.title", new Object[]{}, new Locale("ru")))
                .user(user)
                .event(event)
                .isShown(false)
                .build();
    }

    @Override
    public Notification createPreBookedAdmin(User user, Event event) {
        final Object[] timeArg = {dateToString(event.getDate()), timeToString(event.getTime())};
        return Notification.builder()
                .titleAm(messageSource.getMessage("notification.preBooked.admin.title", new Object[]{}, new Locale("hy")))
                .titleEn(messageSource.getMessage("notification.preBooked.admin.title", new Object[]{}, Locale.US))
                .titleRu(messageSource.getMessage("notification.preBooked.admin.title", new Object[]{}, new Locale("ru")))
                .bodyAm(messageSource.getMessage("notification.preBooked.admin.body", timeArg, new Locale("hy")))
                .bodyEn(messageSource.getMessage("notification.preBooked.admin.body", timeArg, Locale.US))
                .bodyRu(messageSource.getMessage("notification.preBooked.admin.body", timeArg, new Locale("ru")))
                .user(user)
                .event(event)
                .isShown(false)
                .build();
    }

    @Override
    public Notification createPreBookedActivityConfirmed(User user, Event event) {
        return Notification.builder()
                .titleAm(messageSource.getMessage("notification.preBooked.confirmed", new Object[]{}, new Locale("hy")))
                .titleEn(messageSource.getMessage("notification.preBooked.confirmed", new Object[]{}, Locale.US))
                .titleRu(messageSource.getMessage("notification.preBooked.confirmed", new Object[]{}, new Locale("ru")))
                .user(user)
                .event(event)
                .isShown(false)
                .build();
    }

    @Override
    public Notification createPreBookedActivityRescheduled(User user, Event event) {
        final Object[] timeArg = {dateToString(event.getDate()), timeToString(event.getTime())};
        return Notification.builder()
                .titleAm(messageSource.getMessage("notification.preBooked.rescheduled.title", new Object[]{}, new Locale("hy")))
                .titleEn(messageSource.getMessage("notification.preBooked.rescheduled.title", new Object[]{}, Locale.US))
                .titleRu(messageSource.getMessage("notification.preBooked.rescheduled.title", new Object[]{}, new Locale("ru")))
                .bodyAm(messageSource.getMessage("notification.preBooked.rescheduled.body", timeArg, new Locale("hy")))
                .bodyEn(messageSource.getMessage("notification.preBooked.rescheduled.body", timeArg, Locale.US))
                .bodyRu(messageSource.getMessage("notification.preBooked.rescheduled.body", timeArg, new Locale("ru")))
                .user(user)
                .event(event)
                .isShown(false)
                .build();
    }

    @Override
    public Notification createPreBookedActivityDeleted(User user, Event event) {
        final Object[] timeArg = {dateToString(event.getDate()), timeToString(event.getTime())};
        return Notification.builder()
                .titleAm(messageSource.getMessage("notification.preBooked.deleted", timeArg, new Locale("hy")))
                .titleEn(messageSource.getMessage("notification.preBooked.deleted", timeArg, Locale.US))
                .titleRu(messageSource.getMessage("notification.preBooked.deleted", timeArg, new Locale("ru")))
                .user(user)
                .event(event)
                .isShown(false)
                .build();
    }

    @Override
    public Notification createReschedule(User user, Event event) {
        return Notification.builder()
                .titleAm(messageSource.getMessage("notification.reschedule.title", new Object[]{}, new Locale("hy")))
                .titleEn(messageSource.getMessage("notification.reschedule.title", new Object[]{}, Locale.US))
                .titleRu(messageSource.getMessage("notification.reschedule.title", new Object[]{}, new Locale("ru")))
                .user(user)
                .event(event)
                .isShown(false)
                .build();
    }

    @Override
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    private String timeToString(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private String dateToString(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }


    @Transactional
    @Override
    public void saveNotifications(Set<Long> ids) {
        notificationRepository.findAllById(ids).forEach(notification -> notification.setShown(true));
    }

    @Override
    public String getLocalizedTitle(Notification notification) {
        final String language = LocaleContextHolder.getLocale().getLanguage();
        switch (Language.valueOf(language.toUpperCase())) {
            case EN:
                return notification.getTitleEn();
            case RU:
                return notification.getTitleRu();
            default:
                return notification.getTitleAm();
        }
    }

    @Override
    public String getLocalizedBody(Notification notification) {
        final String language = LocaleContextHolder.getLocale().getLanguage();
        switch (Language.valueOf(language.toUpperCase())) {
            case EN:
                return notification.getBodyEn();
            case RU:
                return notification.getBodyRu();
            default:
                return notification.getBodyAm();
        }
    }

    @Override
    public void setUsersAllNotificationsShownAndSeen(User user) {
        List<Notification> listOfNotifications = user.getNotifications();
        for (Notification n : listOfNotifications) {
            if (!n.isSeen()) {
                n.setSeen(true);
                if (n.isShown()) n.setShown(true);
                notificationRepository.save(n);
            }
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    public void remind() {
        final LocalDate eventDay = getArmNowDate().plusDays(1);
        final LocalTime now = getArmNowTime();
        final List<Event> events = eventRepository.findByConfirmedFalseDateAndTimeBetween(eventDay.toString(),
                now.toString(), now.minusHours(1).toString());
        events.stream()
                .filter(event -> !(event.getUser().isAdmin()))
                .map(event -> createReminder(event.getUser(), event))
                .map(this::save)
                .forEach(notification -> {
                    try {
                        final User user = notification.getUser();
                        if (user.getToken() != null && isNotConfirmed(notification)) {
                            messagingService.sendNotification(notification.getUser(), notification);
                            System.out.println("send push notification for user " + user.getUsername());
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                });
    }

    @Scheduled(cron = "0 0 8 * * *")
    //@Scheduled(fixedRate = 86400000)
    public void remindAdminForBlockedUserEvents()
            throws ExecutionException, InterruptedException {
        final List<Event> blockedUserEvents = eventRepository.findAll().stream()
                .filter(e -> e.getUser().getState() == UserState.BLOCKED &&
                        e.getDate().minusDays(2).equals(LocalDate.now()))
                .collect(Collectors.toList());

        final List<User> admins = userRepository.findAll().stream()
                .filter(u -> u.isAdmin() && !u.isSuperAdmin())
                .collect(Collectors.toList());

        for (User admin : admins) {
            for (Event event : blockedUserEvents) {
                final Notification notification = save(createConfirm(admin, event));

                if (admin.getToken() != null && admin.getToken().length() > 0) {
                    messagingService.sendHlaNotification(notification.getUser(), notification);
                }
            }
        }
    }

    @Scheduled(cron = "0 0/15 * * * *")
    @Transactional
    public void delete() {
        final List<Event> events = eventRepository
                .findByDateLessThanEqualAndTimeLessThanEqual(getArmNowDate(), getArmNowTime());
        events
                .stream()
                .filter(event -> !event.getNotifications().isEmpty())
                .forEach(event -> notificationRepository.deleteAllByEventId(event.getId()));
    }

    @Override
    public List<Notification> findByIdCreatedDateDesc(long id) {
        return notificationRepository.findByUser_IdOrderByCreatedDateDesc(id);
    }

    @Override
    public List<Notification> findNotSeenOrNotShownByUserId(long id) {
        return notificationRepository.findByUserIdAndIsSeenFalseOrIsShownFalse(id);
    }

    @Override
    public void delete(Notification notification) {
        notificationRepository.delete(notification);
    }

    public boolean isNotConfirmed(Notification notification) {
        return !notification.getEvent().isConfirmed();
    }

    @Override
    public void findAndDeleteDuplicateOfNotification(Notification notification, User user) {
        getUsersAllNotifications(user)
                .parallelStream()
                .filter(n-> n.equals(notification))
                .forEach(this::delete);
    }
}
