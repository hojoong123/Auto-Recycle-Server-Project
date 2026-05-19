package com.capstone.recycle.DTO.response;

import com.capstone.recycle.Entity.BinStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BinStatusResponse {

    private final Long binId;
    private final Integer fillPercent;
    private final Boolean isFull;
    private final Boolean errorFlag;
    private final LocalDateTime updatedAt;

    public BinStatusResponse(BinStatus status) {
        this.binId = status.getBin().getId();
        this.fillPercent = status.getFillPercent();
        this.isFull = status.getIsFull();
        this.errorFlag = status.getErrorFlag();
        this.updatedAt = status.getUpdatedAt();
    }
}