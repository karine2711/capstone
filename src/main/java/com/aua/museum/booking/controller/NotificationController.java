package com.aua.museum.booking.controller;

import com.aua.museum.booking.domain.Event;
import com.aua.museum.booking.domain.Notification;
import com.aua.museum.booking.domain.User;
import com.aua.museum.booking.service.NotificationService;
import com.aua.museum.booking.service.UserService;
import com.aua.museum.booking.domain.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@RequestMapping("notification")
public class NotificationController {

    private final UserService userService;
    private final NotificationService notificationService;

    @GetMapping
    public ModelAndView getNotificationsByPrincipal(Principal principal, Locale locale) {
        final User user = userService.getUserByUsername(principal.getName());
        final List<Notification> nots = notificationService.findByIdCreatedDateDesc(user.getId());
        notificationService.setUsersAllNotificationsShownAndSeen(user);
        List<Map<Object, Object>> notifications = nots
                .stream()
                .map(notification -> notificationToViewMap(notification, locale))
                .collect(Collectors.toList());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(Templates.NOTIFICATION_CENTER.getName());
        modelAndView.addObject("usersNotifications", notifications);
        return modelAndView;
    }

    @GetMapping("/unseen/{username}")
    public ResponseEntity<List<Map<String, Object>>> getUnseenNotificationsByPrincipal(@PathVariable String username, Principal principal) {
        if (principal != null && principal.getName().equals(username)) {
            final User user = userService.getUserByUsername(principal.getName());
//            if (user.isAdmin()) return ResponseEntity.noContent().build();
            final List<Notification> nots = notificationService.findNotSeenOrNotShownByUserId(user.getId());
            List<Map<String, Object>> notifications = nots
                    .stream()
                    .filter(notification -> !notification.isShown() || !notification.isSeen())
                    .map(this::notificationToMap)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(notifications);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    private Map<Object, Object> notificationToViewMap(Notification notif, Locale locale) {
        Map<Object, Object> map = new HashMap<>();
        map.put("event", notif.getEvent());
        map.put("eventTypeValue", notif.getEvent().getEventType().getValueByLocale(locale));
        map.put("eventTypeId", notif.getEvent().getEventType().getId());
        map.put("title", notif.getTitleByLocale(locale));
        map.put("body", notif.getBodyByLocale(locale));
        return map;
    }

//    @GetMapping(path = "/{username}")
//    @ResponseBody
//    public ResponseEntity<List<Notification>> getNotificationsByUsername(@PathVariable String username) {
//        final User user = userService.getUserByUsername(username);
//        List<Notification> notifications = user.getNotifications()
//                .stream()
//                .filter(notification -> !notification.isShown())
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(notifications);
//    }

    @PutMapping(path = "/{username}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String setNotificationsSeen(@RequestBody Set<Long> ids) {
        notificationService.saveNotifications(ids);
        return "success";
    }

    private Map<String, Object> notificationToMap(Notification notification) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", notification.getId());
        map.put("title", notificationService.getLocalizedTitle(notification));
        map.put("body", notificationService.getLocalizedBody(notification));
        map.put("shown", notification.isShown());
        map.put("seen", notification.isSeen());
        final Event event = notification.getEvent();
        if (event != null) {
            map.put("event", new HashMap<>() {
                {
                    put("id", event.getId());
                    put("date", event.getDate());
                    put("time", event.getTime());
                    put("type", event.getEventType().getId());
                }
            });
        }
        return map;
    }
}

