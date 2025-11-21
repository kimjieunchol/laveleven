package com.labelai.service;

import com.labelai.dto.request.ChangePasswordRequest;
import com.labelai.dto.response.UserResponse;
import com.labelai.entity.User;
import com.labelai.exception.CustomException;
import com.labelai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 서비스
 * 프로필 조회, 비밀번호 변경 등
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자 프로필 조회
     */
    public UserResponse getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("NOT_FOUND", "사용자를 찾을 수 없습니다.", 404));

        return convertToResponse(user);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("NOT_FOUND", "사용자를 찾을 수 없습니다.", 404));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new CustomException("INVALID_PASSWORD", "현재 비밀번호가 일치하지 않습니다.", 401);
        }

        // 새 비밀번호로 변경
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * Entity -> DTO 변환
     */
    private UserResponse convertToResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.isActive())
                .build();
    }
}
