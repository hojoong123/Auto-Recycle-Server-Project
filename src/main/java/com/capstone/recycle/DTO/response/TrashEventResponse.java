package com.capstone.recycle.DTO.response;

import com.capstone.recycle.Entity.TrashEvent;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class TrashEventResponse {
    private Long id;
    private String deviceCode;
    private String binCode;
    private String trashTypeCode;
    private String trashTypeName;
    private Boolean isDefective;
    private String status;
    private String defectReason;
    private Double confidence;
    private String imageUrl;
    private LocalDateTime createdAt;

    public TrashEventResponse(TrashEvent event) {
        this.id = event.getId();
        this.deviceCode = event.getDevice().getDeviceCode();
        this.binCode = event.getBin().getBinCode();
        this.trashTypeCode = event.getTrashType().getTypeCode();
        this.trashTypeName = event.getTrashType().getTypeName();
        this.isDefective = event.getIsDefective();
        this.status = event.getStatus();
        this.defectReason = event.getDefectReason();
        this.confidence = event.getConfidence();
        this.imageUrl = event.getImageUrl();
        this.createdAt = event.getCreatedAt();
    }
}
