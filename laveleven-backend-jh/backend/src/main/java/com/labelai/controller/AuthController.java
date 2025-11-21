package com.labelai.controller;

import com.labelai.dto.request.LoginRequest;
import com.labelai.dto.response.LoginResponse;
import com.labelai.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.labelai.dto.request.ForgotUsernameRequest; // New Import
import com.labelai.dto.request.ForgotPasswordRequest; // New Import
import com.labelai.dto.request.ResetPasswordRequest; // New Import

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 로그인
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 로그아웃
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // 토큰 기반이므로 서버에서는 특별한 처리 없음
        return ResponseEntity.ok().build();
    }

    /**
     * 아이디 찾기 요청
     * POST /api/auth/forgot-username
     */
    @PostMapping("/forgot-username")
    public ResponseEntity<Void> forgotUsername(@RequestBody ForgotUsernameRequest request) {
        authService.forgotUsername(request);
        return ResponseEntity.ok().build();
    }

    /**
     * 비밀번호 재설정 링크 요청
     * POST /api/auth/forgot-password-request
     */
    @PostMapping("/forgot-password-request")
    public ResponseEntity<Void> forgotPasswordRequest(@RequestBody ForgotPasswordRequest request) {
        authService.forgotPasswordRequest(request);
        return ResponseEntity.ok().build();
    }

    /**
     * 비밀번호 재설정
     * POST /api/auth/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }
}