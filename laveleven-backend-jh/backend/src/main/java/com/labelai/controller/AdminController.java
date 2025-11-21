package com.labelai.controller;

import com.labelai.dto.request.CreateUserRequest;
import com.labelai.dto.response.HistoryResponse;
import com.labelai.dto.response.UserResponse;
import com.labelai.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    /**
     * 전체 사용자 목록
     * GET /api/admin/users
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * 전체 이력 조회
     * GET /api/admin/history
     */
    @GetMapping("/history")
    public ResponseEntity<List<HistoryResponse>> getAllHistory() {
        return ResponseEntity.ok(adminService.getAllHistory());
    }

    /**
     * 사용자 생성
     * POST /api/admin/users
     */
    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        UserResponse user = adminService.createUser(request);
        return ResponseEntity.ok(user);
    }

    /**
     * 사용자 삭제
     * DELETE /api/admin/users/{id}
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 사용자 상세 조회
     * GET /api/admin/users/{id}
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserDetail(@PathVariable UUID id) {
        UserResponse user = adminService.getUserDetail(id);
        return ResponseEntity.ok(user);
    }

    /**
     * 특정 사용자의 이력 조회
     * GET /api/admin/users/{userId}/history
     */
    @GetMapping("/users/{id}/history")
    public ResponseEntity<List<HistoryResponse>> getUserHistory(@PathVariable UUID id) {
        return ResponseEntity.ok(adminService.getUserHistory(id));
    }
}