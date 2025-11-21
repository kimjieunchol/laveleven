package com.labelai.controller;

import com.labelai.dto.request.ChangePasswordRequest;
import com.labelai.dto.response.HistoryResponse;
import com.labelai.dto.response.UserResponse;
import com.labelai.entity.Item;
import com.labelai.entity.User;
import com.labelai.exception.CustomException;
import com.labelai.repository.UserRepository;
import com.labelai.service.HistoryService;
import com.labelai.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*; // Restore missing Spring web annotations imports

import java.time.format.DateTimeFormatter;
import java.util.List; // Restore missing List import
import java.util.UUID; // Restore missing UUID import
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class); // Manual logger injection


    private final UserService userService;
    private final HistoryService historyService;
    private final UserRepository userRepository; // User 객체를 찾기 위해 추가

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(Authentication auth) {
        String username = auth.getName();
        UserResponse response = userService.getProfile(username);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            Authentication auth,
            @RequestBody ChangePasswordRequest request
    ) {
        String username = auth.getName();
        userService.changePassword(username, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history")
    public ResponseEntity<List<HistoryResponse>> getMyHistory(Authentication auth) {
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("NOT_FOUND", "User not found", 404));

        List<Item> items = historyService.getItemsForUser(user);
        List<HistoryResponse> response = items.stream()
                .map(this::convertItemToHistoryResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private HistoryResponse convertItemToHistoryResponse(Item item) {
        log.info("Converting item to HistoryResponse. Item ID: {}, CreatedAt: {}", item.getItemId(), item.getCreatedAt()); // Add this log
        return HistoryResponse.builder()
                .id(item.getItemId().toString()) // UUID를 String으로 변환
                .type(item.getItemType())
                .fileName(item.getItemName())
                .date(item.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .time(item.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                .status("completed") // 상태는 ERD에 없으므로 임시값 사용
                .userId(item.getCreatedBy().getUsername())
                .build();
    }

    @DeleteMapping("/history")
    public ResponseEntity<Void> deleteHistory(
            Authentication auth,
            @RequestBody List<UUID> ids
    ) {
        // TODO: Implement history deletion logic using item IDs (UUID)
        return ResponseEntity.ok().build();
    }
}
