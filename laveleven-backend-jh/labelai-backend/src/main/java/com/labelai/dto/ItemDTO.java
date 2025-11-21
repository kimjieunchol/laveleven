package com.labelai.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDTO {
    private String id;
    private String name;              // itemName
    private String type;              // itemType
    private String department;        // departmentId
    private String version;           // version string (e.g., "1.0.0")
    private Integer versionMajor;
    private Integer versionMinor;
    private Integer versionPatch;
    private String description;
    private String createdBy;         // username
    private Long createdById;         // user id
    private String updatedBy;         // username
    private Long updatedById;         // user id
    private String createdAt;
    private String updatedAt;
}