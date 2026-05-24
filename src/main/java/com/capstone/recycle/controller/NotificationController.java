package com.capstone.recycle.controller;

import com.capstone.recycle.DTO.request.InspectionDoneDto;
import com.capstone.recycle.DTO.request.InspectionRequestDto;
import com.capstone.recycle.DTO.response.NotificationResponse;
import com.capstone.recycle.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 웹(총괄) → 해당 층 관리자에게 점검 요청
    @PostMapping("/inspection-request")
    public ResponseEntity<Void> sendInspectionRequest(
            @RequestHeader("X-Admin-Id") Long adminId,
            @RequestBody InspectionRequestDto request) {
        notificationService.sendInspectionRequest(adminId, request);
        return ResponseEntity.ok().build();
    }

    // 앱(현장) → 총괄 관리자에게 점검 완료 보고
    @PostMapping("/inspection-done")
    public ResponseEntity<Void> sendInspectionDone(
            @RequestHeader("X-Admin-Id") Long adminId,
            @RequestBody InspectionDoneDto request) {
        notificationService.sendInspectionDone(adminId, request);
        return ResponseEntity.ok().build();
    }

    // 내 알림 목록
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> list(
            @RequestHeader("X-Admin-Id") Long adminId) {
        return ResponseEntity.ok(notificationService.list(adminId));
    }

    // 미확인 개수
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> unread(
            @RequestHeader("X-Admin-Id") Long adminId) {
        return ResponseEntity.ok(Map.of("count", notificationService.countUnread(adminId)));
    }

    // 읽음 처리
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long id) {
        notificationService.markRead(id);
        return ResponseEntity.ok().build();
    }

    // 확인(총괄이 처리 완료 처리)
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<Void> confirm(@PathVariable Long id) {
        notificationService.confirm(id);
        return ResponseEntity.ok().build();
    }
}
