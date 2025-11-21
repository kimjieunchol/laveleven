package com.labelai.security;

import com.labelai.entity.Item;
import com.labelai.repository.ItemRepository;
import com.labelai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class OwnershipAspect {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Around("@annotation(checkOwnership)")
    public Object checkOwnership(ProceedingJoinPoint joinPoint, CheckOwnership checkOwnership) throws Throwable {
        CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        
        String role = currentUser.getRole();
        
        // SUPER_ADMIN은 모든 접근 허용
        if ("SUPER_ADMIN".equals(role)) {
            return joinPoint.proceed();
        }
        
        Object[] args = joinPoint.getArgs();
        
        if (checkOwnership.value() == OwnershipType.ITEM) {
            String itemId = extractItemId(args);
            if (itemId != null) {
                Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다"));
                
                if ("ADMIN".equals(role)) {
                    if (!item.getDepartmentId().equals(currentUser.getDepartmentId())) {
                        throw new AccessDeniedException("다른 부서의 작업에 접근할 수 없습니다");
                    }
                } else if ("USER".equals(role)) {
                    if (!item.getCreatedBy().getId().equals(currentUser.getUserId())) {
                        throw new AccessDeniedException("본인이 생성한 작업만 접근할 수 있습니다");
                    }
                }
            }
        } else if (checkOwnership.value() == OwnershipType.USER) {
            Long targetUserId = extractUserId(args);
            if (targetUserId != null) {
                if ("ADMIN".equals(role)) {
                    var targetUser = userRepository.findById(targetUserId)
                        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
                    if (!targetUser.getDepartmentId().equals(currentUser.getDepartmentId())) {
                        throw new AccessDeniedException("다른 부서의 사용자에 접근할 수 없습니다");
                    }
                } else if ("USER".equals(role)) {
                    if (!targetUserId.equals(currentUser.getUserId())) {
                        throw new AccessDeniedException("본인의 정보만 접근할 수 있습니다");
                    }
                }
            }
        }
        
        return joinPoint.proceed();
    }

    private String extractItemId(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof String && arg.toString().matches("[a-f0-9-]{36}")) {
                return (String) arg;
            }
        }
        return null;
    }

    private Long extractUserId(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof Long) {
                return (Long) arg;
            }
        }
        return null;
    }
}
