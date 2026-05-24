package com.capstone.recycle.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InspectionRequestDto {
    private Integer floor;     // 점검 요청 대상 층
    private Long deviceId;     // (옵션) 특정 장치
    private Long binId;        // (옵션) 특정 통
    private String message;    // (옵션) 추가 메시지
}
