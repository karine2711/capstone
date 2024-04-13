package com.aua.museum.booking.repository;

import com.aua.museum.booking.domain.WeekDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeekDayRepository extends JpaRepository<WeekDay, Long> {

    @Query("select w.display_value_EN from WeekDay w")
    List<String> getAllWeekDaysInEnglish();

    @Query("select w.display_value_RU from WeekDay w")
    List<String> getAllWeekDaysInRussian();

    @Query("select w.display_value_AM from WeekDay w")
    List<String> getAllWeekDaysInArmenian();
}
