package com.aua.museum.booking.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Locale;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "EVENT_TYPE")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class EventType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(name = "duration", nullable = false)
    private int duration;

    @Column(name = "display_value_EN", nullable = false)
    private String displayValue_EN;

    @Column(name = "display_value_RU", nullable = false)
    private String displayValue_RU;

    @Column(name = "display_value_AM", nullable = false)
    private String displayValue_AM;

    //    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "eventType", cascade = CascadeType.ALL)
    private Set<Event> events;

    public String getValueByLocale(Locale locale) {
        switch (locale.getLanguage().toUpperCase()) {
            case "RU":
                return displayValue_RU;
            case "EN":
                return displayValue_EN;
            default:
                return displayValue_AM;
        }
    }
}
