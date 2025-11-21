package com.labelai.dto.response;

import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Null 값은 JSON에 포함하지 않음
public class AuditLogResponse {
    private UUID historyId;
    private UUID itemId;
    private String stepName;
    private String fieldName;
    private String actionType;
    private JsonNode payload; // Use JsonNode to represent the JSON payload
    private String changedByUsername; // Changed by user's username
    private OffsetDateTime changedAt;
}
