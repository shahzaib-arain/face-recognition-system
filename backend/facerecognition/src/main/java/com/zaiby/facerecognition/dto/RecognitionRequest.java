package com.zaiby.facerecognition.dto;

import lombok.Data;

@Data
public class RecognitionRequest {
    private String imageBase64;
    private String cameraId;
}
