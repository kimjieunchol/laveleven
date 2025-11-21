package com.labelai.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class History {
    @Id
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    
    @Column(name = "step_name", nullable = false)
    private String stepName;
    
    @Column(name = "field_name", nullable = false)
    private String fieldName;
    
    @Column(name = "action_type", nullable = false)
    private String actionType;
    
    @Column(columnDefinition = "JSON")
    private String payload;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by", nullable = false)
    private User changedBy;
    
    @Column(name = "changed_at")
    private LocalDateTime changedAt;
    
    @PrePersist
    void onCreate() { changedAt = LocalDateTime.now(); }
}
