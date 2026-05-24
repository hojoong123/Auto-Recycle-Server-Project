package com.capstone.recycle.DTO.response;

import com.capstone.recycle.Entity.InspectionNotification;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class NotificationResponse {
    private final Long id;
    private final String type;
    private final String status;
    private final String title;
    private final String message;
    private final Integer floor;
    private final Long deviceId;
    private final Long binId;
    private final Long senderId;
    private final String senderName;
    private final Long receiverId;
    private final LocalDateTime sentAt;
    private final LocalDateTime readAt;
    private final LocalDateTime confirmedAt;

    public NotificationResponse(InspectionNotification n) {
        this.id = n.getId();
        this.type = n.getNotificationType();
        this.status = n.getStatus();
        this.title = n.getTitle();
        this.message = n.getMessage();
        this.floor = n.getFloor();
        this.deviceId = n.getDevice() != null ? n.getDevice().getId() : null;
        this.binId = n.getBin() != null ? n.getBin().getId() : null;
        this.senderId = n.getSender().getId();
        this.senderName = n.getSender().getName();
        this.receiverId = n.getReceiver().getId();
        this.sentAt = n.getSentAt();
        this.readAt = n.getReadAt();
        this.confirmedAt = n.getConfirmedAt();
    }
}
