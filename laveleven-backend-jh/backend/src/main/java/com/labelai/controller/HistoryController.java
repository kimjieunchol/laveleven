package com.labelai.controller;

import com.labelai.dto.response.AuditLogResponse; // New Import
import com.labelai.dto.response.ItemDetailResponse;
import com.labelai.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List; // New Import
import java.util.UUID;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDetailResponse> getItemHistoryDetail(@PathVariable UUID itemId) {
        return ResponseEntity.ok(historyService.getItemDetail(itemId));
    }

    @GetMapping("/{itemId}/audit-logs")
    public ResponseEntity<List<AuditLogResponse>> getItemAuditLogs(@PathVariable UUID itemId) {
        return ResponseEntity.ok(historyService.getAuditLogsForItem(itemId));
    }
}

