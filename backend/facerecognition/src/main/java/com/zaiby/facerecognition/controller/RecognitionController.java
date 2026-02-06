package com.zaiby.facerecognition.controller;

import com.zaiby.facerecognition.dto.RecognitionRequest;
import com.zaiby.facerecognition.dto.RecognitionResponse;
import com.zaiby.facerecognition.service.RecognitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/face")
@RequiredArgsConstructor
public class RecognitionController {

    private final RecognitionService recognitionService;

    @PostMapping("/recognize")
    public ResponseEntity<RecognitionResponse> recognize(@RequestBody RecognitionRequest request) {
        return ResponseEntity.ok(recognitionService.recognizeFace(request));
    }
}
