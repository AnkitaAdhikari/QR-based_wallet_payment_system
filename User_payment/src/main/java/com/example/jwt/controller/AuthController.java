package com.example.jwt.controller;

import com.example.jwt.dto.*;
import com.example.jwt.service.AuthService;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        String token = authService.register(request);

        return ResponseEntity.ok(
                Collections.singletonMap("token", token)
        );
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyAccount(@RequestBody VerificationRequest request) {

        authService.verifyAccount(request);

        return ResponseEntity.ok("Account verified successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        String token = authService.login(request);

        return ResponseEntity.ok(
                Collections.singletonMap("token", token)
        );
    }
}