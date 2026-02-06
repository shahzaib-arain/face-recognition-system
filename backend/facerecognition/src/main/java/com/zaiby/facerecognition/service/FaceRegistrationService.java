package com.zaiby.facerecognition.service;

import com.zaiby.facerecognition.dto.*;
import com.zaiby.facerecognition.model.User;
import com.zaiby.facerecognition.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FaceRegistrationService {

    private final AiEmbeddingService aiService;
    private final VectorStore vectorStore;
    private final UserRepository userRepository;

    public FaceRegisterResponse registerFace(FaceRegisterRequest request) {

        // 1️⃣ Check user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2️⃣ Generate embedding via AI service
        EmbeddingResponse embeddingResponse =
                aiService.generateEmbedding(request.getImageBase64());

        List<Double> embedding = embeddingResponse.getEmbedding();

        // 3️⃣ Convert embedding → Spring AI Document
        Document document = new Document(
                "Face embedding for user " + user.getUserId()
        );
        document.getMetadata().put("userId", user.getUserId().toString());

        // 4️⃣ Store embedding in Milvus
        vectorStore.add(List.of(document), embedding);

        return FaceRegisterResponse.builder()
                .userId(user.getUserId())
                .message("Face registered successfully in Milvus")
                .build();
    }
}
