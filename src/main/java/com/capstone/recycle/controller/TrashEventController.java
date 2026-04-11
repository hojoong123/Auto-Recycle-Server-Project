package com.capstone.recycle.controller;

import com.capstone.recycle.dto.request.TrashEventRequest;
import com.capstone.recycle.dto.response.TrashEventResponse;
import com.capstone.recycle.service.TrashEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TrashEventController {

    private final TrashEventService trashEventService;

    // POST /api/events  ← 라즈베리파이 전용
    @PostMapping("/events")
    public ResponseEntity<Void> receiveEvent(@RequestBody TrashEventRequest request) {
        trashEventService.receiveEvent(request);
        return ResponseEntity.ok().build();
    }

    // GET /api/logs
    @GetMapping("/logs")
    public ResponseEntity<List<TrashEventResponse>> getAllEvents() {
        return ResponseEntity.ok(trashEventService.getAllEvents());
    }

    // GET /api/logs/{id}
    @GetMapping("/logs/{id}")
    public ResponseEntity<TrashEventResponse> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(trashEventService.getEvent(id));
    }
}
