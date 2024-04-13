package com.aua.museum.booking.repository;

import com.aua.museum.booking.domain.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventTypeRepository extends JpaRepository<EventType, Integer> {

}
