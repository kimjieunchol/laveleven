package com.labelai.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StructureRequest {
    private List<String> texts;
    private String language;
    
    @JsonProperty("raw_data")
    private RawData rawData;  // ✅ 추가
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RawData {
        @JsonProperty("rec_texts")
        private List<String> recTexts;
    }
}