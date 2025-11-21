package com.labelai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HtmlGenerateRequest {
    @JsonProperty("label_data")
    private Map<String, Object> labelData;
    
    private Map<String, Object> data;
    
    @JsonProperty("source_language")
    private String sourceLanguage;
    
    @JsonProperty("target_country")
    private String targetCountry;
    
    private String country;
}