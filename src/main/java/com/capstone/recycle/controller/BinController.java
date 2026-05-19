package com.capstone.recycle.controller;

import com.capstone.recycle.DTO.response.BinResponse;
import com.capstone.recycle.DTO.response.BinStatusResponse;
import com.capstone.recycle.Service.BinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bins")
@RequiredArgsConstructor
public class BinController {

    private final BinService binService;

    @GetMapping("/{deviceId}")
    public ResponseEntity<List<BinResponse>> getBinsByDevice(@PathVariable Long deviceId) {
        return ResponseEntity.ok(binService.getBinsByDevice(deviceId));
    }

    // 👇 Authorization 헤더에서 토큰 받아서 서비스로 넘김
    @PostMapping("/{id}/reset")
    public ResponseEntity<Void> resetBin(@PathVariable Long id,
                                         @RequestHeader(value = "Authorization", required = false) String authHeader) {
        binService.resetBin(id, authHeader);
        return ResponseEntity.ok().build();
    }

    // 현재 통 상태 조회
    @GetMapping("/status/{binId}")
    public ResponseEntity<BinStatusResponse> getBinStatus(@PathVariable Long binId) {
        return ResponseEntity.ok(binService.getBinStatus(binId));
    }
}
