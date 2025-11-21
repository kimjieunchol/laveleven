package com.labelai.dto;

import lombok.*;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PipelineResponse {
    private OcrResponse ocr;
    private Map<String, Object> structuredData;
    private Map<String, Object> translatedData;
    private String html;
}
