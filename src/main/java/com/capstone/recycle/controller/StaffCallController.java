package com.capstone.recycle.controller;

import com.capstone.recycle.Service.NotificationService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffCallController {

    private final NotificationService notificationService;

    @PostMapping("/call")
    public void callStaff(

            @RequestParam(defaultValue = "UNKNOWN")
            String deviceId,

            @RequestParam(defaultValue = "직원 호출")
            String message,

            @RequestParam(defaultValue = "1")
            Integer floor
    ) {

        notificationService.sendStaffCall(
                deviceId,
                message,
                floor
        );

        System.out.println("직원 호출 전송!");
    }
}