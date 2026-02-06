package com.zaiby.facerecognition.service;

import com.zaiby.facerecognition.dto.EmbeddingRequest;
import com.zaiby.facerecognition.dto.EmbeddingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class AiEmbeddingService {

    private final WebClient webClient;

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    public EmbeddingResponse generateEmbedding(String base64Image) {

        EmbeddingRequest request = new EmbeddingRequest();
        request.setImageBase64(base64Image);

        return webClient.post()
                .uri(aiServiceUrl)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(EmbeddingResponse.class)
                .block();  // blocking for simplicity (OK for now)
    }
}
