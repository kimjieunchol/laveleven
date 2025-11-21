package com.labelai.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Item {
    @Id
    private String id;
    
    @Column(name = "item_name", nullable = false)
    private String itemName;
    
    @Column(name = "item_type")
    private String itemType;
    
    @Column(name = "department_id")
    private String departmentId;
    
    @Builder.Default
    @Column(name = "version_major")
    private Integer versionMajor = 1;
    
    @Builder.Default
    @Column(name = "version_minor")
    private Integer versionMinor = 0;
    
    @Builder.Default
    @Column(name = "version_patch")
    private Integer versionPatch = 0;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    
    @PreUpdate
    void onUpdate() { updatedAt = LocalDateTime.now(); }
    
    public String getVersion() {
        return versionMajor + "." + versionMinor + "." + versionPatch;
    }
}
