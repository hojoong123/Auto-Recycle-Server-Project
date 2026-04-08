package com.capstone.recycle.DTO.response;

import com.capstone.recycle.Entity.Device;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class DeviceResponse {
    private Long id;
    private String deviceCode;
    private String deviceName;
    private String location;
    private String status;
    private LocalDateTime updatedAt;

    public DeviceResponse(Device device) {
        this.id = device.getId();
        this.deviceCode = device.getDeviceCode();
        this.deviceName = device.getDeviceName();
        this.location = device.getLocation();
        this.status = device.getStatus();
        this.updatedAt = device.getUpdatedAt();
    }
}
