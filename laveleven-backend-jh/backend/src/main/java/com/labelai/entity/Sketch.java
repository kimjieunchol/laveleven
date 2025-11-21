package com.labelai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

/**
 * SKETCH(레이아웃/HTML) 단계 스냅샷 엔티티 (ERD 기반)
 */
@Entity
@Table(name = "sketch")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sketch {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "sketch_id", updatable = false, nullable = false)
    private UUID sketchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String data;
}
