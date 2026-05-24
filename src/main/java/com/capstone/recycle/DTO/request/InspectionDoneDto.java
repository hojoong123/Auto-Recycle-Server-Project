package com.capstone.recycle.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InspectionDoneDto {
    private Integer floor;
    private Long deviceId;
    private Long binId;
    private String message;
}
