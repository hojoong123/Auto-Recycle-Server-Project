package com.capstone.recycle.Service;

import com.capstone.recycle.DTO.response.BinResponse;
import com.capstone.recycle.Entity.Bin;
import com.capstone.recycle.Entity.BinStatus;
import com.capstone.recycle.Repository.BinRepository;
import com.capstone.recycle.Repository.BinStatusRepository;
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
    public void resetBin(Long binId) {
        BinStatus status = binStatusRepository.findByBinId(binId)
                .orElseThrow(() -> new IllegalArgumentException("통 상태를 찾을 수 없습니다."));
        status.setFillPercent(0);
        status.setIsFull(false);
        status.setErrorFlag(false);
        status.setLastCollectedAt(LocalDateTime.now());
        binStatusRepository.save(status);

        // ✅ 리셋 후 실시간 브로드캐스트
        Bin bin = binRepository.findById(binId)
                .orElseThrow(() -> new IllegalArgumentException("통을 찾을 수 없습니다."));
        BinResponse binResponse = new BinResponse(bin, status);
        webSocketService.broadcastBinStatus(bin.getDevice().getId(), binResponse);
    }
}
