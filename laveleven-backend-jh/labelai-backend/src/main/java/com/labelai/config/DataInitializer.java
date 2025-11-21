package com.labelai.config;

import com.labelai.entity.User;
import com.labelai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 시작 시 초기 사용자 데이터를 생성합니다.
 * 이미 존재하는 사용자는 건너뜁니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        initializeSuperAdmin();
        initializeTestUsers();
    }
    
    private void initializeSuperAdmin() {
        String username = "superadmin";
        
        if (userRepository.existsByUsername(username)) {
            log.info("SUPER_ADMIN 사용자가 이미 존재합니다: {}", username);
            return;
        }
        
        User superAdmin = User.builder()
            .username(username)
            .email("superadmin@labelai.com")
            .password(passwordEncoder.encode("admin123"))  // 초기 비밀번호
            .role("SUPER_ADMIN")
            .departmentId(null)  // SUPER_ADMIN은 부서 없음
            .isActive(true)
            .build();
        
        userRepository.save(superAdmin);
        log.info("✅ SUPER_ADMIN 생성 완료: username={}, password=admin123", username);
    }
    
    private void initializeTestUsers() {
        // ADMIN 테스트 계정
        createUserIfNotExists(
            "admin1",
            "admin1@labelai.com",
            "admin123",
            "ADMIN",
            "DEPT_A"
        );
        
        // USER 테스트 계정
        createUserIfNotExists(
            "user1",
            "user1@labelai.com",
            "user123",
            "USER",
            "DEPT_A"
        );
        
        createUserIfNotExists(
            "user2",
            "user2@labelai.com",
            "user123",
            "USER",
            "DEPT_B"
        );
    }
    
    private void createUserIfNotExists(String username, String email, 
                                       String password, String role, String department) {
        if (userRepository.existsByUsername(username)) {
            log.info("사용자가 이미 존재합니다: {}", username);
            return;
        }
        
        User user = User.builder()
            .username(username)
            .email(email)
            .password(passwordEncoder.encode(password))
            .role(role)
            .departmentId(department)
            .isActive(true)
            .build();
        
        userRepository.save(user);
        log.info("✅ 테스트 사용자 생성: username={}, role={}, password={}", 
                username, role, password);
    }
}