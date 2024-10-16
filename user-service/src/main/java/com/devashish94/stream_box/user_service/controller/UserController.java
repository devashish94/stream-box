package com.devashish94.stream_box.user_service.controller;

import com.devashish94.stream_box.user_service.dto.UserDto;
import com.devashish94.stream_box.user_service.entity.User;
import com.devashish94.stream_box.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    private ResponseEntity<User> createUser(@RequestBody UserDto user) {
        System.out.println("Incoming: " + user);
        User createdUser = userService.createUser(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

}
