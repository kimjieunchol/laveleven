package com.labelai.controller;

import com.labelai.dto.LoginRequest;
import com.labelai.dto.LoginResponse;
import com.labelai.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            log.info("[LOGIN_ATTEMPT] username={}", request.getUsername());
            LoginResponse result = userService.login(request);
            log.info("[LOGIN_SUCCESS] username={}", request.getUsername());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("[LOGIN_FAILED] username={}, reason={}", request.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            log.warn("[LOGIN_FAILED] username={}, reason={}", request.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/check-duplicate")
    public ResponseEntity<?> checkDuplicate(@RequestParam String username) {
        boolean exists = userService.checkUsernameExists(username);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        // TODO: Refresh Token 로직 구현
        return ResponseEntity.ok(Map.of("message", "Refresh token logic not implemented yet"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // JWT는 stateless이므로 클라이언트에서 토큰 삭제
        log.info("[LOGOUT] Client-side token removal");
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}