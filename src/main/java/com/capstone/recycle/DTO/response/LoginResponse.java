package com.capstone.recycle.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Long adminId;      // ✅ 추가
    private String username;
    private String name;
    private String role;
    private Integer floor;     // ✅ 추가
}
