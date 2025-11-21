package com.labelai.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WorkRequest {
    private String id;
    private String title;
    private String productName;
    private String createdBy;
    private String version;
    private String team;
}
