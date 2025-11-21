package com.labelai.dto;

import lombok.Data;
import java.util.List;

@Data
public class OcrResponse {
    private String filename;
    private String language;
    private List<String> texts;
    private RawData rawData;
    
    @Data
    public static class RawData {
        private List<String> recTexts;
        private List<List<List<Integer>>> boxes;
        private List<Double> confidences;
        private Double averageConfidence;
        private String language;
        private Integer totalLines;
    }
    
    // 기존 코드와 호환을 위한 편의 메서드
    public String getText() {
        return texts != null ? String.join("\n", texts) : "";
    }
    
    public Double getConfidence() {
        return rawData != null ? rawData.getAverageConfidence() : 0.0;
    }
}