package com.labelai.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "schema_data")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SchemaData {
    @Id
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    
    @Column(nullable = false, columnDefinition = "JSON")
    private String data;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    void onCreate() { createdAt = LocalDateTime.now(); }
}
