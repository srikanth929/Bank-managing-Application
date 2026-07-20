package com.finbank.service;

import com.finbank.dto.request.LoginRequest;
import com.finbank.dto.request.RegisterRequest;
import com.finbank.dto.response.AuthResponse;

public interface AuthService {
    void register(RegisterRequest request);
    boolean verifyOtp(String email, String otp);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
    void forgotPassword(String email);
    void resetPassword(String email, String token, String newPassword);
}