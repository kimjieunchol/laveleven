package com.labelai.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scan")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Scan {
    @Id
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    
    @Column(name = "scan_image_url", nullable = false, columnDefinition = "TEXT")
    private String scanImageUrl;
    
    @Column(name = "scan_meta", columnDefinition = "JSON")
    private String scanMeta;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    void onCreate() { createdAt = LocalDateTime.now(); }
}
