package com.aua.museum.booking.repository;

import com.aua.museum.booking.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query(value = "select * from `notification` n where n.user_id=:id and (n.shown=0 or n.seen=0)",
            nativeQuery = true)
    List<Notification> findByUserIdAndIsSeenFalseOrIsShownFalse(@Param("id") Long id);

    List<Notification> findByUser_Id(Long id);

    List<Notification> findByUser_IdOrderByCreatedDateDesc(Long id);

    @Modifying
    @Query(value = "delete  from `notification` where `notification`.`event_id`=:id",
            nativeQuery = true)
    void deleteAllByEventId(Long id);
}
