package com.capstone.recycle.dto.response;

import com.capstone.recycle.entity.BinErrorLog;
import com.capstone.recycle.entity.DeviceErrorLog;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ErrorLogResponse {
    private Long id;
    private String type;       // "BIN" or "DEVICE"
    private String targetCode; // binCode or deviceCode
    private String errorType;
    private String message;
    private Boolean resolved;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    public ErrorLogResponse(BinErrorLog log) {
        this.id = log.getId();
        this.type = "BIN";
        this.targetCode = log.getBin().getBinCode();
        this.errorType = log.getErrorType();
        this.message = log.getMessage();
        this.resolved = log.getResolved();
        this.createdAt = log.getCreatedAt();
        this.resolvedAt = log.getResolvedAt();
    }

    public ErrorLogResponse(DeviceErrorLog log) {
        this.id = log.getId();
        this.type = "DEVICE";
        this.targetCode = log.getDevice().getDeviceCode();
        this.errorType = log.getErrorType();
        this.message = log.getMessage();
        this.resolved = log.getResolved();
        this.createdAt = log.getCreatedAt();
        this.resolvedAt = log.getResolvedAt();
    }
}
