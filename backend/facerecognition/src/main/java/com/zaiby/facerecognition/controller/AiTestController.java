package com.zaiby.facerecognition.controller;

import com.zaiby.facerecognition.dto.EmbeddingResponse;
import com.zaiby.facerecognition.service.AiEmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiTestController {

    private final AiEmbeddingService aiService;

    @PostMapping("/test")
    public ResponseEntity<EmbeddingResponse> testEmbedding(@RequestBody String base64Image) {
        return ResponseEntity.ok(aiService.generateEmbedding(base64Image));
    }
}
