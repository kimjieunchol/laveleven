package com.labelai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
 * SCAN 단계 스냅샷 엔티티 (ERD 기반)
 */
@Entity
@Table(name = "scan")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Scan {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "scan_id", updatable = false, nullable = false)
    private UUID scanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "scan_image_url", nullable = false, columnDefinition = "TEXT")
    private String scanImageUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "scan_meta", nullable = false, columnDefinition = "jsonb")
    private String scanMeta;
}
