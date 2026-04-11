package com.capstone.recycle.controller;

import com.capstone.recycle.dto.response.ErrorLogResponse;
import com.capstone.recycle.service.ErrorLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/errors")
@RequiredArgsConstructor
public class ErrorLogController {

    private final ErrorLogService errorLogService;

    // GET /api/errors
    @GetMapping
    public ResponseEntity<List<ErrorLogResponse>> getAllErrors() {
        return ResponseEntity.ok(errorLogService.getAllErrors());
    }

    // POST /api/errors/bin/{id}/resolve
    @PostMapping("/bin/{id}/resolve")
    public ResponseEntity<Void> resolveBinError(@PathVariable Long id) {
        errorLogService.resolveBinError(id);
        return ResponseEntity.ok().build();
    }

    // POST /api/errors/device/{id}/resolve
    @PostMapping("/device/{id}/resolve")
    public ResponseEntity<Void> resolveDeviceError(@PathVariable Long id) {
        errorLogService.resolveDeviceError(id);
        return ResponseEntity.ok().build();
    }
}
