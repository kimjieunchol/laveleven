package com.labelai.controller;

import com.labelai.dto.ItemDTO;
import com.labelai.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    
    private final ItemService itemService;
    
    /**
     * 아이템 목록 조회 (권한별 자동 필터링)
     */
    @GetMapping
    public ResponseEntity<List<ItemDTO>> getAllItems() {
        try {
            List<ItemDTO> items = itemService.getAllItems();
            log.info("[ITEM_LIST] count={}", items.size());
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            log.error("[ITEM_LIST_ERROR] {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 아이템 단건 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getItemById(@PathVariable String id) {
        try {
            ItemDTO item = itemService.getItemById(id);
            log.info("[ITEM_GET] id={}", id);
            return ResponseEntity.ok(item);
        } catch (RuntimeException e) {
            log.warn("[ITEM_GET_ERROR] id={}, reason={}", id, e.getMessage());
            return ResponseEntity.status(403)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 아이템 검색 (권한별 자동 필터링)
     */
    @GetMapping("/search")
    public ResponseEntity<List<ItemDTO>> searchItems(@RequestParam String keyword) {
        try {
            List<ItemDTO> items = itemService.searchItems(keyword);
            log.info("[ITEM_SEARCH] keyword={}, count={}", keyword, items.size());
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            log.error("[ITEM_SEARCH_ERROR] keyword={}, reason={}", keyword, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 아이템 생성 (SUPER_ADMIN, ADMIN만 가능)
     */
    @PostMapping
    public ResponseEntity<?> createItem(@RequestBody ItemDTO dto) {
        try {
            ItemDTO item = itemService.createItem(dto);
            log.info("[ITEM_CREATED] id={}, name={}", item.getId(), item.getName());
            return ResponseEntity.ok(item);
        } catch (RuntimeException e) {
            log.warn("[ITEM_CREATE_ERROR] name={}, reason={}", dto.getName(), e.getMessage());
            return ResponseEntity.status(403)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 아이템 수정 (SUPER_ADMIN, ADMIN만 가능)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable String id, @RequestBody ItemDTO dto) {
        try {
            ItemDTO item = itemService.updateItem(id, dto);
            log.info("[ITEM_UPDATED] id={}", id);
            return ResponseEntity.ok(item);
        } catch (RuntimeException e) {
            log.warn("[ITEM_UPDATE_ERROR] id={}, reason={}", id, e.getMessage());
            return ResponseEntity.status(403)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 아이템 삭제 (SUPER_ADMIN, ADMIN만 가능)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable String id) {
        try {
            itemService.deleteItem(id);
            log.info("[ITEM_DELETED] id={}", id);
            return ResponseEntity.ok()
                .body(Map.of("message", "아이템이 삭제되었습니다."));
        } catch (RuntimeException e) {
            log.warn("[ITEM_DELETE_ERROR] id={}, reason={}", id, e.getMessage());
            return ResponseEntity.status(403)
                .body(Map.of("error", e.getMessage()));
        }
    }
}