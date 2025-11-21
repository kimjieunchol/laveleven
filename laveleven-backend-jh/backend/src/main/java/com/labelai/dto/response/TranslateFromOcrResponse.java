package com.labelai.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TranslateFromOcrResponse {
    private Object translatedData;
    private String targetCountry;
}
