package com.labelai.service;

import com.labelai.dto.HistoryDTO;
import com.labelai.entity.History;
import com.labelai.entity.Item;
import com.labelai.entity.User;
import com.labelai.repository.HistoryRepository;
import com.labelai.repository.ItemRepository;
import com.labelai.repository.UserRepository;
import com.labelai.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoryService {
    
    private final HistoryRepository historyRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final PermissionService permissionService;
    
    /**
     * 전체 이력 조회 (권한별 필터링)
     */
    public List<HistoryDTO> getAllHistories() {
        CustomUserDetails currentUser = permissionService.getCurrentUser();
        List<History> histories;
        
        if (permissionService.isSuperAdmin()) {
            // SUPER_ADMIN: 모든 이력 조회
            histories = historyRepository.findAll();
            log.info("SUPER_ADMIN이 모든 이력 조회: count={}", histories.size());
        } else if (permissionService.isAdmin()) {
            // ADMIN: 같은 부서 이력만 조회
            histories = historyRepository.findByDepartmentIdOrderByChangedAtDesc(
                currentUser.getDepartmentId());
            log.info("ADMIN이 부서 이력 조회: department={}, count={}", 
                currentUser.getDepartmentId(), histories.size());
        } else {
            // USER: 자기가 생성한 아이템의 이력만 조회
            histories = historyRepository.findByCreatedByUserIdOrderByChangedAtDesc(
                currentUser.getUserId());
            log.info("USER가 자신의 이력 조회: userId={}, count={}", 
                currentUser.getUserId(), histories.size());
        }
        
        return histories.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * 특정 아이템의 이력 조회
     */
    public List<HistoryDTO> getHistoriesByItem(String itemId) {
        if (!permissionService.canViewHistory(itemId)) {
            throw new RuntimeException("해당 아이템의 이력을 조회할 권한이 없습니다.");
        }
        
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("아이템을 찾을 수 없습니다."));
        
        List<History> histories = historyRepository.findByItemOrderByChangedAtDesc(item);
        
        log.info("아이템 이력 조회: itemId={}, count={}", itemId, histories.size());
        return histories.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * 특정 아이템의 특정 단계 이력 조회
     */
    public List<HistoryDTO> getHistoriesByItemAndStep(String itemId, String stepName) {
        if (!permissionService.canViewHistory(itemId)) {
            throw new RuntimeException("해당 아이템의 이력을 조회할 권한이 없습니다.");
        }
        
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("아이템을 찾을 수 없습니다."));
        
        List<History> histories = historyRepository.findByItemAndStepNameOrderByChangedAtDesc(
            item, stepName);
        
        log.info("아이템 단계별 이력 조회: itemId={}, step={}, count={}", 
            itemId, stepName, histories.size());
        return histories.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * 이력 생성 (권한 범위 내에서 모두 생성 가능!)
     */
    @Transactional
    public HistoryDTO createHistory(String itemId, HistoryDTO dto) {
        // 아이템 수정 권한이 있으면 이력 생성 가능
        if (!permissionService.canEditHistory(itemId)) {
            throw new RuntimeException("해당 아이템의 이력을 생성할 권한이 없습니다.");
        }
        
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("아이템을 찾을 수 없습니다."));
        
        CustomUserDetails currentUser = permissionService.getCurrentUser();
        User changer = userRepository.findById(currentUser.getUserId())
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        History history = History.builder()
            .id(UUID.randomUUID().toString())
            .item(item)
            .stepName(dto.getStepName())
            .fieldName(dto.getFieldName())
            .actionType(dto.getActionType())
            .payload(dto.getPayload())
            .changedBy(changer)
            .build();
        
        History savedHistory = historyRepository.save(history);
        log.info("이력 생성 성공: itemId={}, step={}, action={}, changedBy={}", 
            itemId, dto.getStepName(), dto.getActionType(), changer.getUsername());
        
        return convertToDTO(savedHistory);
    }
    
    /**
     * 이력 단건 조회
     */
    public HistoryDTO getHistoryById(String id) {
        History history = historyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("이력을 찾을 수 없습니다."));
        
        if (!permissionService.canViewHistory(history.getItem().getId())) {
            throw new RuntimeException("해당 이력을 조회할 권한이 없습니다.");
        }
        
        log.info("이력 조회 성공: historyId={}", id);
        return convertToDTO(history);
    }
    
    /**
     * Entity -> DTO 변환
     */
    private HistoryDTO convertToDTO(History history) {
        return HistoryDTO.builder()
            .id(history.getId())
            .itemId(history.getItem().getId())
            .itemName(history.getItem().getItemName())
            .stepName(history.getStepName())
            .fieldName(history.getFieldName())
            .actionType(history.getActionType())
            .payload(history.getPayload())
            .changedBy(history.getChangedBy().getUsername())
            .changedAt(history.getChangedAt().toString())
            .build();
    }
}