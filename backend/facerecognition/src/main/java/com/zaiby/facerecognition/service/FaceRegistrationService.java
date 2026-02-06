package com.zaiby.facerecognition.service;

import com.zaiby.facerecognition.dto.*;
import com.zaiby.facerecognition.model.User;
import com.zaiby.facerecognition.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FaceRegistrationService {

    private final AiEmbeddingService aiService;
    private final MilvusService milvusService;
    private final UserRepository userRepository;

    public FaceRegisterResponse registerFace(FaceRegisterRequest request) {

        // 1️⃣ Verify user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2️⃣ Generate embedding from AI service
        EmbeddingResponse embeddingResponse =
                aiService.generateEmbedding(request.getImageBase64());

        List<Double> embedding = embeddingResponse.getEmbedding();

        // 3️⃣ Store embedding in Milvus
        milvusService.insertEmbedding(user.getUserId(), embedding);

        return FaceRegisterResponse.builder()
                .userId(user.getUserId())
                .message("Face registered successfully in Milvus")
                .build();
    }
}
