package com.aua.museum.booking.repository;

import com.aua.museum.booking.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

}
