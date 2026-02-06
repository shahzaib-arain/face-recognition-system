package com.zaiby.facerecognition.controller;

import com.zaiby.facerecognition.dto.FaceRegisterRequest;
import com.zaiby.facerecognition.dto.FaceRegisterResponse;
import com.zaiby.facerecognition.service.FaceRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/face")
@RequiredArgsConstructor
public class FaceRegistrationController {

    private final FaceRegistrationService faceService;

    @PostMapping("/register")
    public ResponseEntity<FaceRegisterResponse> registerFace(
            @RequestBody FaceRegisterRequest request) {

        return ResponseEntity.ok(faceService.registerFace(request));
    }
}
