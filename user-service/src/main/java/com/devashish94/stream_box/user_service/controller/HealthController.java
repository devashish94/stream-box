package com.devashish94.stream_box.user_service.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/health")
public class HealthController {

    @GetMapping("/ping")
    public ResponseEntity<?> userServiceHealth(@RequestHeader(value = "X-USER-ID") String userId) {
        System.out.println("userId: " + userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new Ping("ok", HttpStatus.OK, "User Service is running."));
    }
}

record Ping(String status, HttpStatus statusCode, String message) {
}
