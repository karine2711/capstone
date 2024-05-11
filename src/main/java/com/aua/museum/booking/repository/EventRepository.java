package com.aua.museum.booking.repository;

import com.aua.museum.booking.domain.Event;
import com.aua.museum.booking.domain.EventType;
import com.aua.museum.booking.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(value = "SELECT * FROM museum.event e where  e.date=:date and e.time = :time ",
            nativeQuery = true)
    List<Event> findByDateAndTime(LocalDate date, LocalTime time);

    List<Event> findByDateBetweenAndEventTypeInOrderByDateAscTimeAsc(LocalDate startDate, LocalDate endDate, List<EventType> eventTypes);

    List<Event> findByDateBetweenAndUserAndEventTypeInOrderByDateAscTimeAsc(LocalDate startDate, LocalDate endDate, User user, List<EventType> eventTypes);

    @Query(value = "SELECT * FROM museum.event e where e.date=:date and (e.time between :startTime and :endTime)",
            nativeQuery = true)
    List<Event> findByDateAndTimeBetween(@Param("date") String date, @Param("startTime") String startTime,
                                         @Param("endTime") String endTime);


    List<Event> findByDateAndTimeBetween(LocalDate date, LocalTime startTime, LocalTime endTime);

    @Query(value = "SELECT * FROM museum.event e where e.event_state='BOOKED' and e.date=:date and (e.time between :startTime and :endTime)",
            nativeQuery = true)
    List<Event> findByConfirmedFalseDateAndTimeBetween(@Param("date") String date, @Param("startTime") String startTime,
                                                       @Param("endTime") String endTime);

    List<Event> findByDateLessThanEqualAndTimeLessThanEqual(LocalDate date, LocalTime time);

    @Query(value = "SELECT * FROM museum.event e JOIN museum.user u on e.user_id==u.id " +
            " where u.user_state='BLOCKED' and e.event_state='PRE_BOOKED'",
            nativeQuery = true)
    List<Event> findBlockedUsersPreBooked();

    @Query(value = "SELECT * FROM museum.event e JOIN museum.user u on e.user_id==u.id " +
            " where u.user_state='ACTIVE' and e.event_state='PRE_BOOKED'",
            nativeQuery = true)
    List<Event> findActiveUsersPreBooked();
}
