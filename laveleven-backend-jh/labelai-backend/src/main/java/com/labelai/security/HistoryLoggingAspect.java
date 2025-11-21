package com.labelai.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.labelai.entity.History;
import com.labelai.entity.Item;
import com.labelai.entity.User;
import com.labelai.repository.HistoryRepository;
import com.labelai.repository.ItemRepository;
import com.labelai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class HistoryLoggingAspect {
    private final HistoryRepository historyRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @AfterReturning(
        pointcut = "execution(* com.labelai.service.PipelineService.save*(..)) " +
                  "|| execution(* com.labelai.service.ItemService.createItem(..)) " +
                  "|| execution(* com.labelai.service.ItemService.deleteItem(..))",
        returning = "result"
    )
    public void logChange(JoinPoint joinPoint, Object result) {
        try {
            CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
            
            String methodName = joinPoint.getSignature().getName();
            Object[] args = joinPoint.getArgs();
            
            String itemId = extractItemId(args, result);
            if (itemId == null) return;
            
            Item item = itemRepository.findById(itemId).orElse(null);
            if (item == null) return;
            
            User user = userRepository.findById(currentUser.getUserId()).orElse(null);
            if (user == null) return;
            
            String stepName = determineStepName(methodName);
            String actionType = determineActionType(methodName);
            String fieldName = determineFieldName(methodName);
            
            History history = History.builder()
                .id(UUID.randomUUID().toString())
                .item(item)
                .stepName(stepName)
                .fieldName(fieldName)
                .actionType(actionType)
                .payload(objectMapper.writeValueAsString(args))
                .changedBy(user)
                .build();
            
            historyRepository.save(history);
            log.info("[HISTORY] user={}, action={}, item={}, step={}", 
                currentUser.getUsername(), actionType, itemId, stepName);
        } catch (Exception e) {
            log.error("히스토리 로깅 실패", e);
        }
    }

    private String extractItemId(Object[] args, Object result) {
        for (Object arg : args) {
            if (arg instanceof String && arg.toString().matches("[a-f0-9-]{36}")) {
                return (String) arg;
            }
        }
        return null;
    }

    private String determineStepName(String methodName) {
        if (methodName.contains("Scan")) return "SCAN";
        if (methodName.contains("Schema")) return "SCHEMA";
        if (methodName.contains("Translate")) return "TRANSLATE";
        if (methodName.contains("Sketch")) return "SKETCH";
        if (methodName.contains("createItem")) return "ITEM";
        return "UNKNOWN";
    }

    private String determineActionType(String methodName) {
        if (methodName.startsWith("save")) return "SAVE";
        if (methodName.startsWith("update")) return "UPDATE";
        if (methodName.startsWith("delete")) return "DELETE";
        if (methodName.startsWith("create")) return "CREATE";
        return "UNKNOWN";
    }

    private String determineFieldName(String methodName) {
        if (methodName.contains("Scan")) return "scan_data";
        if (methodName.contains("Schema")) return "schema_data";
        if (methodName.contains("Translate")) return "translate_data";
        if (methodName.contains("Sketch")) return "sketch_data";
        return "item";
    }
}
