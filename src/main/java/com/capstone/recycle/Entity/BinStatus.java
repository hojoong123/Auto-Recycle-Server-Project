package com.capstone.recycle.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bin_status")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BinStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bin_id", nullable = false, unique = true)
    private Bin bin;

    @Column(name = "fill_percent", nullable = false)
    @Builder.Default
    private Integer fillPercent = 0;

    @Column(name = "is_full", nullable = false)
    @Builder.Default
    private Boolean isFull = false;

    @Column(name = "error_flag", nullable = false)
    @Builder.Default
    private Boolean errorFlag = false;

    @Column(name = "last_collected_at")
    private LocalDateTime lastCollectedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
