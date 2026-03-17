package com.sms.studentmanagement.controller;

import com.sms.studentmanagement.dto.AuthDTO;
import com.sms.studentmanagement.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    // ---- LOGIN ----
    @PostMapping("/login")
    public ResponseEntity<AuthDTO.LoginResponse>
            authenticateUser(
                @Valid @RequestBody
                    AuthDTO.LoginRequest request) {
        return ResponseEntity.ok(
            authService.login(request));
    }

    // ---- REGISTER ----
    @PostMapping("/register")
    public ResponseEntity<AuthDTO.MessageResponse>
            registerUser(
                @Valid @RequestBody
                    AuthDTO.RegisterRequest request) {
        return ResponseEntity.ok(
            authService.register(request));
    }
}
