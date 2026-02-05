package com.zaiby.facerecognition.service;

import com.zaiby.facerecognition.dto.AttendanceResponse;
import com.zaiby.facerecognition.model.Attendance;
import com.zaiby.facerecognition.model.User;
import com.zaiby.facerecognition.repository.AttendanceRepository;
import com.zaiby.facerecognition.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    public AttendanceResponse markAttendance(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user already checked in
        Attendance activeAttendance =
                attendanceRepository.findTopByUserAndCheckOutTimeIsNullOrderByCheckInTimeDesc(user)
                        .orElse(null);

        if (activeAttendance == null) {
            // CHECK IN
            Attendance attendance = Attendance.builder()
                    .user(user)
                    .checkInTime(LocalDateTime.now())
                    .build();

            attendanceRepository.save(attendance);
            return mapToResponse(attendance);

        } else {
            // CHECK OUT
            activeAttendance.setCheckOutTime(LocalDateTime.now());
            attendanceRepository.save(activeAttendance);
            return mapToResponse(activeAttendance);
        }
    }

    public List<AttendanceResponse> getAllAttendance() {
        return attendanceRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AttendanceResponse mapToResponse(Attendance attendance) {
        return AttendanceResponse.builder()
                .attendanceId(attendance.getAttendanceId())
                .userId(attendance.getUser().getUserId())
                .userName(attendance.getUser().getName())
                .checkInTime(attendance.getCheckInTime())
                .checkOutTime(attendance.getCheckOutTime())
                .build();
    }
}
