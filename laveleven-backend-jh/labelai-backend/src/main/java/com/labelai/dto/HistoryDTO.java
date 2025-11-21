package com.labelai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryDTO {
    private String id;
    private String itemId;
    private String itemName;
    private String stepName;        // SCAN, SCHEMA, TRANSLATE, SKETCH, VIOLATIONS
    private String fieldName;
    private String actionType;      // SAVE, UPDATE, ROLLBACK
    private String payload;         // JSON 문자열
    private String changedBy;
    private String changedAt;
}