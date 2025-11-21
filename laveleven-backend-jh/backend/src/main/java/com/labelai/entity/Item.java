package com.labelai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Add Slf4j
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 라벨 변환 대상 아이템(제품/라벨 파일) 마스터 엔티티 (ERD 기반)
 */
@Entity
@Table(name = "items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j // Add Slf4j annotation
public class Item {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "item_id", updatable = false, nullable = false)
    private UUID itemId;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "item_type", length = 100)
    private String itemType;

    @Column(name = "department_id", length = 50)
    private String departmentId;

    // PostgreSQL의 integer[] 타입에 매핑하기 위해 @Column(columnDefinition = "integer[]") 사용
    // 실제 배열 타입 처리를 위해서는 build.gradle/pom.xml에 의존성 추가가 필요할 수 있음 (e.g., hypersistence-utils)
    @Builder.Default
    @Column(columnDefinition = "integer[]")
    private int[] version = {1, 0, 0};

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    // Manually add getter to log the value
    public OffsetDateTime getCreatedAt() {
        log.info("Accessing Item ID: {} createdAt getter. Value: {}", this.itemId, this.createdAt);
        return createdAt;
    }

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // Manually add getter to log the value
    public OffsetDateTime getUpdatedAt() {
        log.info("Accessing Item ID: {} updatedAt getter. Value: {}", this.itemId, this.updatedAt);
        return updatedAt;
    }
}
