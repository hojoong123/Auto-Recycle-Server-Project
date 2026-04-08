package com.capstone.recycle.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trash_type")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrashType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type_code", nullable = false, unique = true, length = 50)
    private String typeCode; // PLASTIC, CAN, GLASS, GENERAL

    @Column(name = "type_name", length = 100)
    private String typeName;

    @Column(name = "description")
    private String description;

    @Column(name = "recyclable", nullable = false)
    @Builder.Default
    private Boolean recyclable = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
