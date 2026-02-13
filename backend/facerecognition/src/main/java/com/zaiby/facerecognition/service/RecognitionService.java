package com.zaiby.facerecognition.service;

import com.zaiby.facerecognition.dto.*;
import com.zaiby.facerecognition.model.User;
import com.zaiby.facerecognition.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecognitionService {

    private final AiEmbeddingService aiService;
    private final MilvusSearchService milvusSearchService;
    private final UserRepository userRepository;
    private final AttendanceService attendanceService;
    private final AlertService alertService;

    public RecognitionResponse recognizeFace(RecognitionRequest request) {

        // 1️⃣ Generate embedding
        EmbeddingResponse embeddingResponse =
                aiService.generateEmbedding(request.getImageBase64());

        List<Double> embedding = embeddingResponse.getEmbedding();

        // 2️⃣ Search nearest in Milvus
        String userIdStr = milvusSearchService.searchNearest(embedding);

        if (userIdStr != null) {
            Long userId = Long.parseLong(userIdStr);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 3️⃣ Mark attendance automatically
            attendanceService.markAttendance(userId);

            return RecognitionResponse.builder()
                    .userId(userId)
                    .userName(user.getName())
                    .message("Face recognized successfully")
                    .build();

        } else {
            // 4️⃣ Trigger unknown face alert
            alertService.createAlert(
                    new com.zaiby.facerecognition.dto.AlertRequest() {{
                        setCameraId(request.getCameraId());
                        setAlertType("UNKNOWN_FACE");
                    }}
            );

            return RecognitionResponse.builder()
                    .userId(null)
                    .userName("Unknown")
                    .message("Face not recognized")
                    .build();
        }
    }
}
