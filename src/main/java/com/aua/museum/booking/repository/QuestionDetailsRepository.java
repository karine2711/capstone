package com.aua.museum.booking.repository;

import com.aua.museum.booking.domain.QuestionDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionDetailsRepository extends JpaRepository<QuestionDetails, Long> {

    List<QuestionDetails> getAllByUser_Email(String email);

}
