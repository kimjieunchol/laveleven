package com.labelai.service;

import com.labelai.dto.request.CreateUserRequest;
import com.labelai.dto.response.HistoryResponse;
import com.labelai.dto.response.UserResponse;
import com.labelai.entity.Item;
import com.labelai.entity.User;
import com.labelai.exception.CustomException;
import com.labelai.repository.ItemRepository;
import com.labelai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 관리자 서비스
 * 사용자 CRUD, 전체 이력 관리
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ItemRepository itemRepository;

    /**
     * 전체 사용자 목록 조회
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 특정 사용자의 작업 이력 조회
     */
    public List<HistoryResponse> getUserHistory(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("NOT_FOUND", "사용자를 찾을 수 없습니다.", 404));
        return itemRepository.findByCreatedByOrderByCreatedAtDesc(user).stream()
                .map(this::convertItemToHistoryResponse)
                .collect(Collectors.toList());
    }


    /**
     * 전체 작업 이력 조회
     */
    public List<HistoryResponse> getAllHistory() {
        return itemRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::convertItemToHistoryResponse)
                .collect(Collectors.toList());
    }

    /**
     * 사용자 생성
     */
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException("DUPLICATE_USERNAME", "이미 존재하는 아이디입니다.", 409);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("DUPLICATE_EMAIL", "이미 존재하는 이메일입니다.", 409);
        }

        // 비밀번호가 제공되지 않으면 아이디를 기본 비밀번호로 사용
        String rawPassword = StringUtils.hasText(request.getPassword()) ? request.getPassword() : request.getUsername();

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(rawPassword)) // 기본 비밀번호 암호화
                .role(StringUtils.hasText(request.getRole()) ? request.getRole() : "user") // 기본값 'user'
                .departmentId(request.getDepartmentId())
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }

    /**
     * 사용자 삭제
     */
    @Transactional
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException("NOT_FOUND", "사용자를 찾을 수 없습니다.", 404);
        }
        userRepository.deleteById(userId);
    }

    /**
     * 사용자 상세 조회
     */
    public UserResponse getUserDetail(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("NOT_FOUND", "사용자를 찾을 수 없습니다.", 404));

        return convertToResponse(user);
    }

    /**
     * Entity -> DTO 변환
     */
    private UserResponse convertToResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                // .name(user.getUsername()) // Removed as UserResponse no longer has 'name' field
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.isActive())
                .build();
    }

    private HistoryResponse convertItemToHistoryResponse(Item item) {
        return HistoryResponse.builder()
                .id(item.getItemId().toString())
                .type(item.getItemType())
                .fileName(item.getItemName())
                .date(item.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .time(item.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                .status("completed") // Placeholder
                .userId(item.getCreatedBy().getUsername())
                .build();
    }
}