package com.aua.museum.booking.repository;

import com.aua.museum.booking.domain.GeneralInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneralInfoRepository extends JpaRepository<GeneralInfo, Long> {
}
