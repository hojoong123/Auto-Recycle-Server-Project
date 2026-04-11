package com.capstone.recycle.controller;

import com.capstone.recycle.dto.response.BinResponse;
import com.capstone.recycle.service.BinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bins")
@RequiredArgsConstructor
public class BinController {

    private final BinService binService;

    // GET /api/bins/{deviceId}
    @GetMapping("/{deviceId}")
    public ResponseEntity<List<BinResponse>> getBinsByDevice(@PathVariable Long deviceId) {
        return ResponseEntity.ok(binService.getBinsByDevice(deviceId));
    }

    // POST /api/bins/{id}/reset
    @PostMapping("/{id}/reset")
    public ResponseEntity<Void> resetBin(@PathVariable Long id) {
        binService.resetBin(id);
        return ResponseEntity.ok().build();
    }
}
