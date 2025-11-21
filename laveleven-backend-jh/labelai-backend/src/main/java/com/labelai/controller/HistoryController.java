package com.labelai.controller;

import com.labelai.dto.HistoryDTO;
import com.labelai.service.HistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/histories")
@RequiredArgsConstructor
@Slf4j
public class HistoryController {
    
    private final HistoryService historyService;
    
    /**
     * 전체 이력 조회 (권한별 자동 필터링)
     */
    @GetMapping
    public ResponseEntity<List<HistoryDTO>> getAllHistories() {
        try {
            List<HistoryDTO> histories = historyService.getAllHistories();
            log.info("[HISTORY_LIST] count={}", histories.size());
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            log.error("[HISTORY_LIST_ERROR] {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 특정 아이템의 이력 조회
     */
    @GetMapping("/item/{itemId}")
    public ResponseEntity<?> getHistoriesByItem(@PathVariable String itemId) {
        try {
            List<HistoryDTO> histories = historyService.getHistoriesByItem(itemId);
            log.info("[HISTORY_BY_ITEM] itemId={}, count={}", itemId, histories.size());
            return ResponseEntity.ok(histories);
        } catch (RuntimeException e) {
            log.warn("[HISTORY_BY_ITEM_ERROR] itemId={}, reason={}", itemId, e.getMessage());
            return ResponseEntity.status(403)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 특정 아이템의 특정 단계 이력 조회
     */
    @GetMapping("/item/{itemId}/step/{stepName}")
    public ResponseEntity<?> getHistoriesByItemAndStep(
            @PathVariable String itemId, 
            @PathVariable String stepName) {
        try {
            List<HistoryDTO> histories = historyService.getHistoriesByItemAndStep(itemId, stepName);
            log.info("[HISTORY_BY_STEP] itemId={}, step={}, count={}", 
                itemId, stepName, histories.size());
            return ResponseEntity.ok(histories);
        } catch (RuntimeException e) {
            log.warn("[HISTORY_BY_STEP_ERROR] itemId={}, step={}, reason={}", 
                itemId, stepName, e.getMessage());
            return ResponseEntity.status(403)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 이력 생성
     */
    @PostMapping("/item/{itemId}")
    public ResponseEntity<?> createHistory(
            @PathVariable String itemId, 
            @RequestBody HistoryDTO dto) {
        try {
            HistoryDTO history = historyService.createHistory(itemId, dto);
            log.info("[HISTORY_CREATED] itemId={}, step={}", itemId, dto.getStepName());
            return ResponseEntity.ok(history);
        } catch (RuntimeException e) {
            log.warn("[HISTORY_CREATE_ERROR] itemId={}, reason={}", itemId, e.getMessage());
            return ResponseEntity.status(403)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 이력 단건 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getHistoryById(@PathVariable String id) {
        try {
            HistoryDTO history = historyService.getHistoryById(id);
            log.info("[HISTORY_GET] id={}", id);
            return ResponseEntity.ok(history);
        } catch (RuntimeException e) {
            log.warn("[HISTORY_GET_ERROR] id={}, reason={}", id, e.getMessage());
            return ResponseEntity.status(403)
                .body(Map.of("error", e.getMessage()));
        }
    }
}