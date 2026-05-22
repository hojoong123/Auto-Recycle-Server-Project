package com.capstone.recycle.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffCallController {

    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/call")
    public void callStaff() {

        Map<String, Object> data = new HashMap<>();

        data.put("type", "STAFF_CALL");
        data.put("message", "직원 호출 발생");
        data.put("time", LocalDateTime.now());

        // WebSocket 실시간 전송
        messagingTemplate.convertAndSend(
                "/topic/staff",
                data
        );

        System.out.println("🚨 직원 호출 발생");
    }
}