package com.labelai.service;

import com.labelai.dto.request.LoginRequest;
import com.labelai.dto.response.LoginResponse;
import com.labelai.entity.User;
import com.labelai.exception.CustomException;
import com.labelai.repository.UserRepository;
import com.labelai.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.labelai.dto.request.ForgotUsernameRequest;
import com.labelai.dto.request.ForgotPasswordRequest;
import com.labelai.dto.request.ResetPasswordRequest;
// import org.springframework.mail.javamail.JavaMailSender; // For future use

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 인증 서비스
 * 로그인, 로그아웃, 토큰 갱신, 비밀번호/아이디 찾기 처리
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    // private final JavaMailSender mailSender; // Uncomment and configure for actual email sending

    // Temporary storage for password reset tokens
    // In a real application, this should be a database entity with expiration
    private final Map<String, PasswordResetToken> passwordResetTokens = new ConcurrentHashMap<>();

    private static class PasswordResetToken {
        String username;
        LocalDateTime expiryDate;

        PasswordResetToken(String username, LocalDateTime expiryDate) {
            this.username = username;
            this.expiryDate = expiryDate;
        }

        boolean isValid() {
            return LocalDateTime.now().isBefore(expiryDate);
        }
    }

    /**
     * 로그인 처리
     */
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException("NOT_FOUND", "사용자를 찾을 수 없습니다.", 404));

        if (!user.isActive()) {
            throw new CustomException("INACTIVE_USER", "비활성화된 계정입니다.", 403);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new CustomException("INVALID_PASSWORD", "비밀번호가 일치하지 않습니다.", 401);
        }

        // JWT 토큰 생성
        String token = jwtUtil.generateToken(user.getUsername());

        // ERD에 맞는 LoginResponse를 반환 (추후 DTO 수정 필요)
        return LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .name(user.getUsername()) // ERD에 name 필드가 없으므로 username으로 대체
                .email(user.getEmail())
                .role(user.getRole()) // isAdmin -> role
                .isActive(user.isActive()) // isFirstLogin -> isActive
                .build();
    }

    /**
     * 사용자 아이디 찾기
     */
    public void forgotUsername(ForgotUsernameRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("NOT_FOUND", "해당 이메일로 가입된 사용자를 찾을 수 없습니다.", 404));

        // TODO: 실제 이메일 발송 로직 구현 (메일 설정 후 mailSender 주입 및 사용)
        System.out.println("DEBUG: Sending username to " + request.getEmail() + ": " + user.getUsername());
        // SimpleMailMessage message = new SimpleMailMessage();
        // message.setTo(request.getEmail());
        // message.setSubject("Label AI - 아이디 안내");
        // message.setText("귀하의 아이디는: " + user.getUsername() + " 입니다.");
        // mailSender.send(message);
    }

    /**
     * 비밀번호 재설정 요청 (링크 이메일 발송)
     */
    public void forgotPasswordRequest(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("NOT_FOUND", "해당 이메일로 가입된 사용자를 찾을 수 없습니다.", 404));

        String token = UUID.randomUUID().toString();
        // Token valid for 1 hour
        passwordResetTokens.put(token, new PasswordResetToken(user.getUsername(), LocalDateTime.now().plusHours(1)));

        // TODO: 실제 이메일 발송 로직 구현 (메일 설정 후 mailSender 주입 및 사용)
        String resetLink = "http://localhost:3000/reset-password?token=" + token; // Frontend URL
        System.out.println("DEBUG: Sending password reset link to " + request.getEmail() + ": " + resetLink);
        // SimpleMailMessage message = new SimpleMailMessage();
        // message.setTo(request.getEmail());
        // message.setSubject("Label AI - 비밀번호 재설정");
        // message.setText("비밀번호를 재설정하려면 다음 링크를 클릭하세요: " + resetLink);
        // mailSender.send(message);
    }

    /**
     * 비밀번호 재설정
     */
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokens.get(request.getToken());

        if (resetToken == null || !resetToken.isValid()) {
            throw new CustomException("INVALID_TOKEN", "유효하지 않거나 만료된 비밀번호 재설정 토큰입니다.", 400);
        }

        User user = userRepository.findByUsername(resetToken.username)
                .orElseThrow(() -> new CustomException("NOT_FOUND", "사용자를 찾을 수 없습니다.", 404));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        passwordResetTokens.remove(request.getToken()); // Invalidate token after use
    }
}
