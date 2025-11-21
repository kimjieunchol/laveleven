package com.labelai.dto;

import lombok.*;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TranslateResponse {
    private Map<String, Object> translatedData;
    private String targetCountry;
}
