package com.example.facerecognition.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AlertResponse {

    private Long alertId;
    private Long userId;
    private String userName;
    private String cameraId;
    private String alertType;
    private LocalDateTime timestamp;
}
