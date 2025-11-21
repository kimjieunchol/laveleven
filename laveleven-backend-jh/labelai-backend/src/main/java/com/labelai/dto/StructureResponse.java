package com.labelai.dto;

import lombok.*;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StructureResponse {
    private Map<String, Object> data;
}
