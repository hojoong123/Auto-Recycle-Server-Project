package com.capstone.recycle.service;

import com.capstone.recycle.dto.response.BinResponse;
import com.capstone.recycle.entity.BinStatus;
import com.capstone.recycle.repository.BinRepository;
import com.capstone.recycle.repository.BinStatusRepository;
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

    public List<BinResponse> getBinsByDevice(Long deviceId) {
        return binRepository.findByDeviceId(deviceId)
                .stream()
                .map(bin -> {
                    BinStatus status = binStatusRepository.findByBinId(bin.getId()).orElse(null);
                    return new BinResponse(bin, status);
                })
                .collect(Collectors.toList());
    }

    public void resetBin(Long binId) {
        BinStatus status = binStatusRepository.findByBinId(binId)
                .orElseThrow(() -> new IllegalArgumentException("통 상태를 찾을 수 없습니다."));
        status.setFillPercent(0);
        status.setIsFull(false);
        status.setErrorFlag(false);
        status.setLastCollectedAt(LocalDateTime.now());
        binStatusRepository.save(status);
    }
}
