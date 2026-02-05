package com.zaiby.facerecognition.controller;

import com.zaiby.facerecognition.dto.AttendanceResponse;
import com.zaiby.facerecognition.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    // Simulates recognition event
    @PostMapping("/{userId}")
    public ResponseEntity<AttendanceResponse> markAttendance(@PathVariable Long userId) {
        return ResponseEntity.ok(attendanceService.markAttendance(userId));
    }

    @GetMapping
    public ResponseEntity<List<AttendanceResponse>> getAllAttendance() {
        return ResponseEntity.ok(attendanceService.getAllAttendance());
    }
}
