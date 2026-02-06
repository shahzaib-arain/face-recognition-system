package com.zaiby.facerecognition.service;

import com.zaiby.facerecognition.dto.AlertRequest;
import com.example.facerecognition.dto.AlertResponse;
import com.zaiby.facerecognition.model.Alert;
import com.zaiby.facerecognition.model.User;
import com.zaiby.facerecognition.repository.AlertRepository;
import com.zaiby.facerecognition.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;

    public AlertResponse createAlert(AlertRequest request) {

        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId()).orElse(null);
        }

        Alert alert = Alert.builder()
                .user(user)
                .cameraId(request.getCameraId())
                .alertType(request.getAlertType())
                .build();

        alertRepository.save(alert);

        return mapToResponse(alert);
    }

    public List<AlertResponse> getAllAlerts() {
        return alertRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AlertResponse mapToResponse(Alert alert) {
        return AlertResponse.builder()
                .alertId(alert.getAlertId())
                .userId(alert.getUser() != null ? alert.getUser().getUserId() : null)
                .userName(alert.getUser() != null ? alert.getUser().getName() : "Unknown")
                .cameraId(alert.getCameraId())
                .alertType(alert.getAlertType())
                .timestamp(alert.getTimestamp())
                .build();
    }
}
