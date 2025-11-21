package com.labelai.dto;

import lombok.Data;
import java.util.List;

@Data
public class TranslateFromOcrRequest {
    private OcrResponse ocrResult;
    private String language;
    private List<String> texts;
    private String targetCountry;
}
