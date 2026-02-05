package com.zaiby.facerecognition.repository;

import com.zaiby.facerecognition.model.Attendance;
import com.zaiby.facerecognition.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findTopByUserAndCheckOutTimeIsNullOrderByCheckInTimeDesc(User user);
}
