package com.capstone.recycle.Service;

import com.capstone.recycle.DTO.response.BinResponse;
import com.capstone.recycle.Entity.Admin;
import com.capstone.recycle.Entity.Bin;
import com.capstone.recycle.Entity.BinStatus;
import com.capstone.recycle.Entity.TrashEvent;
import com.capstone.recycle.Repository.AdminRepository;
import com.capstone.recycle.Repository.BinRepository;
import com.capstone.recycle.Repository.BinStatusRepository;
import com.capstone.recycle.Repository.TrashEventRepository;
import com.capstone.recycle.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BinService {

    private final BinRepository binRepository;
    private final BinStatusRepository binStatusRepository;
    private final WebSocketService webSocketService;
    private final TrashEventRepository trashEventRepository;
    private final AdminRepository adminRepository;
    private final JwtUtil jwtUtil;

    public List<BinResponse> getBinsByDevice(Long deviceId) {
        return binRepository.findByDeviceId(deviceId)
                .stream()
                .map(bin -> {
                    BinStatus status = binStatusRepository.findByBinId(bin.getId()).orElse(null);
                    return new BinResponse(bin, status);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void resetBin(Long binId, String authHeader) {
        BinStatus status = binStatusRepository.findByBinId(binId)
                .orElseThrow(() -> new IllegalArgumentException("통 상태를 찾을 수 없습니다."));

        int oldPercent = status.getFillPercent() != null ? status.getFillPercent() : 0;

        status.setFillPercent(0);
        status.setIsFull(false);
        status.setErrorFlag(false);
        status.setLastCollectedAt(LocalDateTime.now());
        binStatusRepository.save(status);

        Bin bin = binRepository.findById(binId)
                .orElseThrow(() -> new IllegalArgumentException("통을 찾을 수 없습니다."));

        Admin admin = resolveAdmin(authHeader);

        TrashEvent resetEvent = TrashEvent.builder()
                .device(bin.getDevice())
                .bin(bin)
                .trashType(bin.getTrashType())
                .eventType("RESET")
                .performedBy(admin)
                .status("PROCESSED")
                .isDefective(false)
                .defectReason("통 비우기 (이전 적재량 " + oldPercent + "%)")
                .build();
        trashEventRepository.save(resetEvent);

        BinResponse binResponse = new BinResponse(bin, status);
        webSocketService.broadcastBinStatus(bin.getDevice().getId(), binResponse);
    }

    private Admin resolveAdmin(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            return adminRepository.findByUsername(username).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}
