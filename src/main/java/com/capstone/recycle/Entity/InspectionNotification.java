package com.capstone.recycle.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inspection_notification")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InspectionNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bin_id")
    private Bin bin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_admin_id", nullable = false)
    private Admin sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_admin_id", nullable = false)
    private Admin receiver;

    @Column(name = "floor")
    private Integer floor;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "notification_type", nullable = false, length = 30)
    @Builder.Default
    private String notificationType = "INSPECTION_COMPLETE";
    // INSPECTION_REQUEST / INSPECTION_COMPLETE / FULL_ALERT / ERROR_ALERT

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "SENT"; // SENT / READ / CONFIRMED / CANCELED

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        if (sentAt == null) sentAt = now;
    }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
