package com.labelai.service;

import com.labelai.dto.LoginRequest;
import com.labelai.dto.LoginResponse;
import com.labelai.dto.UserDTO;
import com.labelai.entity.User;
import com.labelai.repository.UserRepository;
import com.labelai.security.CustomUserDetails;
import com.labelai.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PermissionService permissionService;
    
    /**
     * 로그인
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        
        if (!user.getIsActive()) {
            throw new RuntimeException("비활성화된 계정입니다.");
        }
        
        String accessToken = jwtTokenProvider.createToken(
            user.getUsername(), user.getRole(), user.getId(), user.getDepartmentId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUsername());
        
        log.info("로그인 성공: username={}, role={}", user.getUsername(), user.getRole());
        
        return LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .build();
    }
    
    /**
     * 사용자 목록 조회 (권한별 필터링)
     */
    public List<UserDTO> getAllUsers() {
        CustomUserDetails currentUser = permissionService.getCurrentUser();
        List<User> users;
        
        if (permissionService.isSuperAdmin()) {
            // SUPER_ADMIN: 모든 사용자 조회
            users = userRepository.findAll();
            log.info("SUPER_ADMIN이 모든 사용자 조회: count={}", users.size());
        } else if (permissionService.isAdmin()) {
            // ADMIN: 같은 부서 사용자만 조회
            users = userRepository.findByDepartmentId(currentUser.getDepartmentId());
            log.info("ADMIN이 부서 사용자 조회: department={}, count={}", 
                currentUser.getDepartmentId(), users.size());
        } else {
            // USER: 자기 자신만 조회
            User user = userRepository.findById(currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            users = List.of(user);
            log.info("USER가 자신의 정보 조회: userId={}", currentUser.getUserId());
        }
        
        return users.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * 사용자 단건 조회
     */
    public UserDTO getUserById(Long id) {
        if (!permissionService.canViewUser(id)) {
            throw new RuntimeException("해당 사용자를 조회할 권한이 없습니다.");
        }
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        log.info("사용자 조회 성공: userId={}", id);
        return convertToDTO(user);
    }
    
    /**
     * 사용자 생성
     */
    @Transactional
    public UserDTO createUser(UserDTO dto) {
        CustomUserDetails currentUser = permissionService.getCurrentUser();
        
        // ADMIN은 자기 부서에만 사용자 생성 가능
        if (permissionService.isAdmin()) {
            if (dto.getTeam() == null || !dto.getTeam().equals(currentUser.getDepartmentId())) {
                throw new RuntimeException("자신의 부서에만 사용자를 생성할 수 있습니다.");
            }
        }
        
        // USER는 사용자 생성 불가
        if (permissionService.isUser()) {
            throw new RuntimeException("사용자를 생성할 권한이 없습니다.");
        }
        
        if (userRepository.existsByUsername(dto.getUserId())) {
            throw new RuntimeException("이미 존재하는 사용자명입니다.");
        }
        
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        
        // ADMIN은 USER만 생성 가능
        String targetRole = dto.getRole() != null ? dto.getRole() : "USER";
        if (permissionService.isAdmin() && !"USER".equals(targetRole)) {
            throw new RuntimeException("ADMIN은 USER만 생성할 수 있습니다.");
        }
        
        User user = User.builder()
            .username(dto.getUserId())
            .password(passwordEncoder.encode(dto.getPassword()))
            .email(dto.getEmail())
            .role(targetRole)
            .departmentId(dto.getTeam())
            .isActive(true)
            .build();
        
        User savedUser = userRepository.save(user);
        log.info("사용자 생성 성공: userId={}, role={}", savedUser.getId(), savedUser.getRole());
        
        return convertToDTO(savedUser);
    }
    
    /**
     * 사용자 수정
     */
    @Transactional
    public UserDTO updateUser(Long id, UserDTO dto) {
        if (!permissionService.canEditUser(id)) {
            throw new RuntimeException("해당 사용자를 수정할 권한이 없습니다.");
        }
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 이메일 수정
        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("이미 존재하는 이메일입니다.");
            }
            user.setEmail(dto.getEmail());
        }
        
        // 비밀번호 수정
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        
        // SUPER_ADMIN만 역할 변경 가능
        if (permissionService.isSuperAdmin() && dto.getRole() != null) {
            user.setRole(dto.getRole());
        }
        
        User updatedUser = userRepository.save(user);
        log.info("사용자 수정 성공: userId={}", id);
        
        return convertToDTO(updatedUser);
    }
    
    /**
     * 사용자 삭제 (비활성화)
     */
    @Transactional
    public void deleteUser(Long id) {
        if (!permissionService.canEditUser(id)) {
            throw new RuntimeException("해당 사용자를 삭제할 권한이 없습니다.");
        }
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 자기 자신은 삭제 불가
        if (id.equals(permissionService.getCurrentUser().getUserId())) {
            throw new RuntimeException("자기 자신은 삭제할 수 없습니다.");
        }
        
        user.setIsActive(false);
        userRepository.save(user);
        
        log.info("사용자 비활성화 성공: userId={}", id);
    }

    /**
     * 사용자명 중복 체크
     */
    public boolean checkUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
    
    /**
     * Entity -> DTO 변환
     */
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
            .id(user.getId())
            .userId(user.getUsername())
            .name(user.getUsername())
            .email(user.getEmail())
            .role(user.getRole())
            .team(user.getDepartmentId())
            .build();
    }
}