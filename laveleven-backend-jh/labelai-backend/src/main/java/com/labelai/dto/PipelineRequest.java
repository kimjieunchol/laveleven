package com.labelai.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PipelineRequest {
    private MultipartFile image;
    private String targetCountry;
    private Boolean generateHtml;
}
