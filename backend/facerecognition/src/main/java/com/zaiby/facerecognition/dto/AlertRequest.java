package com.zaiby.facerecognition.dto;

import lombok.Data;

@Data
public class AlertRequest {
    private Long userId;      // optional
    private String cameraId;
    private String alertType;
}
