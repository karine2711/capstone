package com.aua.museum.booking.service.notifications;

import com.google.firebase.messaging.*;
import com.aua.museum.booking.domain.Notification;
import com.aua.museum.booking.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.aua.museum.booking.util.ZonedDateTimeUtil.getArmNowTime;

@Service
public class FCMService {

    public String sendDataNotification(String token, Map<String, String> data)
            throws InterruptedException, ExecutionException {
        Message message = Message.builder()
                .putAllData(data)
                .setToken(token)
                .build();
        String response = FirebaseMessaging.getInstance().sendAsync(message).get();
        System.out.println("Sent data message: " + response);
        return response;
    }

    public String sendNotification(String token, Notification notification)
            throws InterruptedException, ExecutionException {
        Message message = Message.builder()
                .putData("sentTime", getArmNowTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                .setToken(token)
//                .setNotification(com.google.firebase.messaging.Notification.builder()
//                        .setTitle("custom title")
//                .setBody("some body").build())
                .setWebpushConfig(WebpushConfig.builder().putHeader("ttl", "300")
                        .setNotification(new WebpushNotification(notification.getTitleEn(),
                                notification.getBodyEn(), "/images/logo.png"))
                        .setFcmOptions(WebpushFcmOptions.withLink(getUrl() + "/notification"))
                        .build())
                .build();
        String response = FirebaseMessaging.getInstance().sendAsync(message).get();
        System.out.println("Sent notification message: " + response);
        return response;
    }

    public String sendHlaNotification(User user, Notification notification)
            throws ExecutionException, InterruptedException {
        Message message = Message.builder()
                .putData("sentTime", getArmNowTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                .setToken(user.getToken())
                .setWebpushConfig(WebpushConfig.builder().putHeader("ttl", "86400")
                        .setNotification(new WebpushNotification(notification.getTitleEn(),
                                notification.getBodyEn(), "/images/logo.png"))
                        .build())
                .build();
        String response = FirebaseMessaging.getInstance().sendAsync(message).get();
        System.out.println("Sent notification message: " + response);
        return response;
    }

    public String sendNotification(User user, Notification notification)
            throws InterruptedException, ExecutionException {
        Message message = Message.builder()
                .putData("sentTime", getArmNowTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                .setToken(user.getToken())
//                .setNotification(com.google.firebase.messaging.Notification.builder()
//                        .setTitle("custom title")
//                .setBody("some body").build())
                .setWebpushConfig(WebpushConfig.builder().putHeader("ttl", "86400")
                        .setNotification(new WebpushNotification(notification.getTitleEn(),
                                notification.getBodyEn(), "/images/logo.png"))
                        .setFcmOptions(WebpushFcmOptions.withLink(getUrl() + "/notification"))
                        .build())
                .build();
        String response = FirebaseMessaging.getInstance().sendAsync(message).get();
        System.out.println("Sent notification message: " + response);
        return response;
    }

    public String sendNotification(String token, Map<String, String> data, Notification notification)
            throws InterruptedException, ExecutionException {

        Message message = Message.builder()
                .putAllData(data)
                .setToken(token)
                .setWebpushConfig(WebpushConfig.builder().putHeader("ttl", "300")
                        .setNotification(new WebpushNotification(notification.getTitleAm(),
                                notification.getBodyAm(), "/images/logo.png"))
                        .setFcmOptions(WebpushFcmOptions.withLink(getUrl() + "/homepage"))
                        .build())
                .build();
        String response = FirebaseMessaging.getInstance().sendAsync(message).get();
        System.out.println("Sent notification message: " + response);
        return response;
    }

    private String getUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }
}
