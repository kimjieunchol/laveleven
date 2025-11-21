package com.labelai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode; // New Import
import com.fasterxml.jackson.databind.ObjectMapper;
import com.labelai.dto.OcrResponse;
import com.labelai.dto.StructureResponse;
import com.labelai.dto.TranslateResponse;
import com.labelai.dto.response.AuditLogResponse; // New Import
import com.labelai.dto.response.ItemDetailResponse;
import com.labelai.entity.*;
import com.labelai.exception.CustomException;
import com.labelai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.time.Instant; // Explicitly import Instant
import java.time.OffsetDateTime; // Explicitly import OffsetDateTime
import java.time.ZoneOffset; // Explicitly import ZoneOffset
import java.util.stream.Collectors; // New Import

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final ItemRepository itemRepository;
    private final ScanRepository scanRepository;
    private final SchemaRepository schemaRepository;
    private final TranslateRepository translateRepository;
    private final SketchRepository sketchRepository;
    private final ObjectMapper objectMapper;

    public List<Item> getItemsForUser(User user) {
        return itemRepository.findByCreatedByOrderByCreatedAtDesc(user);
    }

    @Transactional
    public void addHistoryRecord(Item item, User user, String stepName, String fieldName, String actionType, String payload) {
        OffsetDateTime currentTimestamp = Instant.now().atOffset(ZoneOffset.UTC);
        log.info("Attempting to add history record with changedAt: {}", currentTimestamp);

        History historyRecord = History.builder()
                .item(item)
                .changedBy(user)
                .stepName(stepName)
                .fieldName(fieldName)
                .actionType(actionType)
                .payload(payload)
                .changedAt(currentTimestamp) // Explicitly set the current time in UTC
                .build();
        
        History savedHistory = historyRepository.save(historyRecord);
        log.info("History record saved. Retrieved changedAt from saved entity: {}", savedHistory.getChangedAt());
    }

    public ItemDetailResponse getItemDetail(UUID itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException("NOT_FOUND", "Item not found", 404));

        OcrResponse ocrResponse = null;
        StructureResponse structureResponse = null;
        TranslateResponse translateResponse = null;
        String sketchResult = null;

        try {
            Scan scan = scanRepository.findFirstByItem(item).orElse(null);
            if (scan != null && scan.getScanMeta() != null) {
                // Assuming scanMeta is a JSON string of OcrResponse
                ocrResponse = objectMapper.readValue(scan.getScanMeta(), OcrResponse.class);
            }

            Schema schema = schemaRepository.findFirstByItem(item).orElse(null);
            if (schema != null && schema.getData() != null) {
                // Assuming data is a JSON string of StructureResponse
                structureResponse = objectMapper.readValue(schema.getData(), StructureResponse.class);
            }

            Translate translate = translateRepository.findFirstByItem(item).orElse(null);
            if (translate != null && translate.getData() != null) {
                // Assuming data is a JSON string of TranslateResponse
                translateResponse = objectMapper.readValue(translate.getData(), TranslateResponse.class);
            }

            Sketch sketch = sketchRepository.findFirstByItem(item).orElse(null);
            if (sketch != null) {
                sketchResult = sketch.getData();
                log.info("Retrieved sketchResult for item ID {}: {}", itemId, sketchResult); // Add this logging
            }

        } catch (JsonProcessingException e) {
            log.error("Error deserializing item details for item ID: {}", itemId, e);
            throw new CustomException("DESERIALIZATION_ERROR", "Failed to read item details.", 500);
        }

        return ItemDetailResponse.builder()
                .itemName(item.getItemName())
                .ocrResult(ocrResponse)
                .structureResult(structureResponse)
                .translateResult(translateResponse)
                .sketchResult(sketchResult)
                .build();
    }

    /**
     * 특정 아이템의 변경 이력(Audit Logs)을 조회합니다.
     */
    public List<AuditLogResponse> getAuditLogsForItem(UUID itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException("NOT_FOUND", "Item not found", 404));

        List<AuditLogResponse> auditLogs = historyRepository.findByItemOrderByChangedAtDesc(item).stream()
                .map(history -> AuditLogResponse.builder()
                        .historyId(history.getHistoryId())
                        .itemId(history.getItem().getItemId())
                        .stepName(history.getStepName())
                        .fieldName(history.getFieldName())
                        .actionType(history.getActionType())
                        .payload(parseJsonPayload(history.getPayload()))
                        .changedByUsername(history.getChangedBy().getUsername())
                        .changedAt(history.getChangedAt())
                        .build())
                .collect(Collectors.toList());
        
        // Add logging here, now that auditLogs is a local variable
        if (!auditLogs.isEmpty()) {
            AuditLogResponse firstLog = auditLogs.get(0);
            log.info("Preparing AuditLogResponse for frontend. First log changedAt: {}", firstLog.getChangedAt());
        } else {
            log.info("No audit logs found for item ID: {}", itemId);
        }

        return auditLogs;
    }

    private JsonNode parseJsonPayload(String jsonString) {
        try {
            if (jsonString == null || jsonString.isEmpty()) {
                return null;
            }
            return objectMapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse history payload JSON for item ID: {}. Payload: {}", e);
            return null; // Return null or an empty JsonNode if parsing fails
        }
    }
}