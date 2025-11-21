package com.labelai.controller;

import com.labelai.dto.UserDTO;
import com.labelai.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    private final UserService userService;
    
    /**
     * 사용자 목록 조회 (권한별 자동 필터링)
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        try {
            List<UserDTO> users = userService.getAllUsers();
            log.info("[USER_LIST] count={}", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("[USER_LIST_ERROR] {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 사용자 단건 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        try {
            UserDTO user = userService.getUserById(id);
            log.info("[USER_GET] id={}", id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            log.warn("[USER_GET_ERROR] id={}, reason={}", id, e.getMessage());
            return ResponseEntity.status(403).build();
        }
    }
    
    /**
     * 사용자 생성 (SUPER_ADMIN, ADMIN만 가능)
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserDTO dto) {
        try {
            UserDTO user = userService.createUser(dto);
            log.info("[USER_CREATED] id={}, username={}", user.getId(), user.getUserId());
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            log.warn("[USER_CREATE_ERROR] username={}, reason={}", dto.getUserId(), e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 사용자 수정 (SUPER_ADMIN, ADMIN만 가능)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDTO dto) {
        try {
            UserDTO user = userService.updateUser(id, dto);
            log.info("[USER_UPDATED] id={}", id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            log.warn("[USER_UPDATE_ERROR] id={}, reason={}", id, e.getMessage());
            return ResponseEntity.status(403)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 사용자 삭제 (비활성화)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            log.info("[USER_DELETED] id={}", id);
            return ResponseEntity.ok()
                .body(Map.of("message", "사용자가 비활성화되었습니다."));
        } catch (RuntimeException e) {
            log.warn("[USER_DELETE_ERROR] id={}, reason={}", id, e.getMessage());
            return ResponseEntity.status(403)
                .body(Map.of("error", e.getMessage()));
        }
    }
}