package com.zaiby.facerecognition.dto;

import lombok.Data;

@Data
public class EmbeddingRequest {
    private String imageBase64;
}