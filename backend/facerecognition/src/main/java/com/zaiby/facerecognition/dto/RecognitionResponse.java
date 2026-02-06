package com.zaiby.facerecognition.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecognitionResponse {
    private Long userId;       // null if unknown
    private String userName;   // "Unknown" if not found
    private String message;
}
