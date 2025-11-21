package com.labelai.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslateFromStructureRequest {
    private Map<String, Object> data;
    private String targetCountry;
    private String language; // Add language field
}
