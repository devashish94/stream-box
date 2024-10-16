package com.devashish94.video_processing_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/process")
public class VideoProcessController {

    @GetMapping
    public ResponseEntity<?> processVideo() {
        return ResponseEntity.ok("Starting Processing for video: ");
    }

}
