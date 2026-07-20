package com.finbank.controller;

import com.finbank.dto.request.LoginRequest;
import com.finbank.dto.request.RegisterRequest;
import com.finbank.dto.response.ApiResponse;
import com.finbank.dto.response.AuthResponse;
import com.finbank.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Registration Successful. Verification OTP dispatched via log simulator.")
                .build());
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        boolean success = authService.verifyOtp(email, otp);
        if (success) {
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Account Activated Completely.")
                    .build());
        }
        return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                .success(false)
                .message("Invalid OTP code or expired.")
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse token = authService.login(request);
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Authentication Successful")
                .data(token)
                .build());
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestParam String refreshToken) {
        AuthResponse token = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Token Refreshed Successfully")
                .data(token)
                .build());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestParam String email) {
        authService.forgotPassword(email);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Simulated recovery protocol dispatched successfully")
                .build());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestParam String email,
            @RequestParam String token,
            @RequestParam String newPassword
    ) {
        authService.resetPassword(email, token, newPassword);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Password updated. You may login now.")
                .build());
    }
}