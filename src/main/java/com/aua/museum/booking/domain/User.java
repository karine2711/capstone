package com.aua.museum.booking.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
@Table(name = "USER")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class User extends BaseEntity {

    @Column(name = "username", nullable = false, unique = true)
    private String username;
    @Column(name = "full_name", nullable = false)
    private String fullName;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "phone", nullable = false)
    private String phone;
    @Column(name = "school")
    private String school;
    @Column(name = "occupation")
    private String occupation;
    @Column(name = "residency")
    private String residency;
    @Column(name = "address")
    private String address;
    @Lob
    @Column(name = "photo", columnDefinition = "BLOB")
    private byte[] profileAvatar;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "user_state")
    private UserState state = UserState.ACTIVE;

    //    @Column(name = "blocked", nullable = false)
//    private int state;
    @Column(name = "token")
    @EqualsAndHashCode.Exclude
    private String token;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "role")
    private Role role;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<QuestionDetails> questionsDetails = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @EqualsAndHashCode.Exclude
    private Set<Event> events;
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @EqualsAndHashCode.Exclude
    private List<Notification> notifications = new ArrayList<>();

    @Builder
    public User(Long id, Timestamp createdDate, Timestamp lastModifiedDate,
                String username, String fullName, String email, String password,
                String phone, String school, String occupation, String residency,
                String address) {
        super(id, createdDate, lastModifiedDate);
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.school = school;
        this.occupation = occupation;
        this.residency = residency;
        this.address = address;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void addNotification(Notification notification) {
        notifications.add(notification);
    }

    public boolean isAdmin() {
        return Role.ADMIN_ROLE.equals(role);
    }

    public boolean isSuperAdmin() {
        return Role.SUPER_ADMIN_ROLE.equals(role);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public void setResidency(String residency) {
        this.residency = residency;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setProfileAvatar(byte[] profileAvatar) {
        this.profileAvatar = profileAvatar;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public void setQuestionsDetails(List<QuestionDetails> questionsDetails) {
        this.questionsDetails = questionsDetails;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}
