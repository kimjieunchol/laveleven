package com.labelai.service;

import com.labelai.entity.Item;
import com.labelai.entity.User;
import com.labelai.repository.ItemRepository;
import com.labelai.repository.UserRepository;
import com.labelai.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PermissionService {
    
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    
    /**
     * 현재 로그인한 사용자 정보 가져오기
     */
    public CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) auth.getPrincipal();
        }
        throw new RuntimeException("인증되지 않은 사용자입니다.");
    }
    
    /**
     * SUPER_ADMIN 권한 체크
     */
    public boolean isSuperAdmin() {
        return "SUPER_ADMIN".equals(getCurrentUser().getRole());
    }
    
    /**
     * ADMIN 권한 체크
     */
    public boolean isAdmin() {
        return "ADMIN".equals(getCurrentUser().getRole());
    }
    
    /**
     * USER 권한 체크
     */
    public boolean isUser() {
        return "USER".equals(getCurrentUser().getRole());
    }
    
    /**
     * 사용자 조회 권한 체크
     */
    public boolean canViewUser(Long targetUserId) {
        CustomUserDetails currentUser = getCurrentUser();
        
        // SUPER_ADMIN: 모든 사용자 조회 가능
        if (isSuperAdmin()) {
            return true;
        }
        
        User targetUser = userRepository.findById(targetUserId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // ADMIN: 같은 부서 사용자만 조회 가능
        if (isAdmin()) {
            return targetUser.getDepartmentId() != null && 
                   targetUser.getDepartmentId().equals(currentUser.getDepartmentId());
        }
        
        // USER: 자기 자신만 조회 가능
        return targetUserId.equals(currentUser.getUserId());
    }
    
    /**
     * 사용자 수정 권한 체크
     */
    public boolean canEditUser(Long targetUserId) {
        CustomUserDetails currentUser = getCurrentUser();
        
        // SUPER_ADMIN: 모든 사용자 수정 가능
        if (isSuperAdmin()) {
            return true;
        }
        
        User targetUser = userRepository.findById(targetUserId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // ADMIN: 같은 부서의 사용자 수정 가능
        if (isAdmin()) {
            return targetUser.getDepartmentId() != null && 
                   targetUser.getDepartmentId().equals(currentUser.getDepartmentId());
        }
        
        // USER: 자기 자신만 수정 가능
        return targetUserId.equals(currentUser.getUserId());
    }
    
    /**
     * 사용자 삭제 권한 체크
     */
    public boolean canDeleteUser(Long targetUserId) {
        CustomUserDetails currentUser = getCurrentUser();
        
        // 자기 자신은 삭제 불가
        if (targetUserId.equals(currentUser.getUserId())) {
            return false;
        }
        
        // SUPER_ADMIN: 모든 사용자 삭제 가능
        if (isSuperAdmin()) {
            return true;
        }
        
        User targetUser = userRepository.findById(targetUserId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // ADMIN: 같은 부서의 사용자 삭제 가능
        if (isAdmin()) {
            return targetUser.getDepartmentId() != null && 
                   targetUser.getDepartmentId().equals(currentUser.getDepartmentId());
        }
        
        // USER: 삭제 불가
        return false;
    }
    
    /**
     * 아이템 조회 권한 체크
     */
    public boolean canViewItem(String itemId) {
        CustomUserDetails currentUser = getCurrentUser();
        
        // SUPER_ADMIN: 모든 아이템 조회 가능
        if (isSuperAdmin()) {
            return true;
        }
        
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("아이템을 찾을 수 없습니다."));
        
        // ADMIN: 같은 부서 아이템만 조회 가능
        if (isAdmin()) {
            return item.getDepartmentId() != null && 
                   item.getDepartmentId().equals(currentUser.getDepartmentId());
        }
        
        // USER: 자기가 생성한 아이템만 조회 가능
        return item.getCreatedBy().getId().equals(currentUser.getUserId());
    }
    
    /**
     * 아이템 수정 권한 체크 (모두 수정 가능!)
     */
    public boolean canEditItem(String itemId) {
        CustomUserDetails currentUser = getCurrentUser();
        
        // SUPER_ADMIN: 모든 아이템 수정 가능
        if (isSuperAdmin()) {
            return true;
        }
        
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("아이템을 찾을 수 없습니다."));
        
        // ADMIN: 같은 부서 아이템 수정 가능
        if (isAdmin()) {
            return item.getDepartmentId() != null && 
                   item.getDepartmentId().equals(currentUser.getDepartmentId());
        }
        
        // USER: 자기가 생성한 아이템 수정 가능
        return item.getCreatedBy().getId().equals(currentUser.getUserId());
    }
    
    /**
     * 아이템 삭제 권한 체크 (모두 삭제 가능!)
     */
    public boolean canDeleteItem(String itemId) {
        CustomUserDetails currentUser = getCurrentUser();
        
        // SUPER_ADMIN: 모든 아이템 삭제 가능
        if (isSuperAdmin()) {
            return true;
        }
        
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("아이템을 찾을 수 없습니다."));
        
        // ADMIN: 같은 부서 아이템 삭제 가능
        if (isAdmin()) {
            return item.getDepartmentId() != null && 
                   item.getDepartmentId().equals(currentUser.getDepartmentId());
        }
        
        // USER: 자기가 생성한 아이템 삭제 가능
        return item.getCreatedBy().getId().equals(currentUser.getUserId());
    }
    
    /**
     * 히스토리 조회 권한 체크
     */
    public boolean canViewHistory(String itemId) {
        // 아이템 조회 권한과 동일
        return canViewItem(itemId);
    }
    
    /**
     * 히스토리 생성/수정 권한 체크
     */
    public boolean canEditHistory(String itemId) {
        // 아이템 수정 권한과 동일
        return canEditItem(itemId);
    }
}