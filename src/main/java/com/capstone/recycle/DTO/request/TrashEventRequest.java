package com.capstone.recycle.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TrashEventRequest {
    private String deviceCode;
    private String binCode;
    private String trashTypeCode;
    private Boolean isDefective;
    private String defectReason;
    private Double confidence;
    private String imageUrl;
    private Integer fillPercent;
}
