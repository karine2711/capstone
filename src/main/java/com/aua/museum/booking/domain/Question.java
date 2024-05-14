package com.aua.museum.booking.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "question")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @Column(name = "description_EN", nullable = false)
    private String descriptionEN;

    @Column(name = "description_RU", nullable = false)
    private String descriptionRU;

    @Column(name = "description_AM", nullable = false)
    private String descriptionAM;

    @OneToMany(mappedBy = "question")
    @JsonIgnore
    @ToString.Exclude
    private List<QuestionDetails> questionDetails = new ArrayList<>();

}
