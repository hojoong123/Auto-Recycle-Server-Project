package com.capstone.recycle.Service;

import com.capstone.recycle.DTO.request.TrashEventRequest;
import com.capstone.recycle.DTO.response.TrashEventResponse;
import com.capstone.recycle.Entity.*;
import com.capstone.recycle.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrashEventService {

    private final TrashEventRepository trashEventRepository;
    private final DeviceRepository deviceRepository;
    private final BinRepository binRepository;
    private final TrashTypeRepository trashTypeRepository;
    private final BinStatusRepository binStatusRepository;

    // 라즈베리파이에서 이벤트 수신
    public void receiveEvent(TrashEventRequest request) {
        Device device = deviceRepository.findByDeviceCode(request.getDeviceCode())
                .orElseThrow(() -> new IllegalArgumentException("장치를 찾을 수 없습니다: " + request.getDeviceCode()));

        Bin bin = binRepository.findByBinCode(request.getBinCode())
                .orElseThrow(() -> new IllegalArgumentException("통을 찾을 수 없습니다: " + request.getBinCode()));

        TrashType trashType = trashTypeRepository.findByTypeCode(request.getTrashTypeCode())
                .orElseThrow(() -> new IllegalArgumentException("쓰레기 종류를 찾을 수 없습니다: " + request.getTrashTypeCode()));

        // 이벤트 저장
        TrashEvent event = TrashEvent.builder()
                .device(device)
                .bin(bin)
                .trashType(trashType)
                .isDefective(request.getIsDefective())
                .defectReason(request.getDefectReason())
                .confidence(request.getConfidence())
                .imageUrl(request.getImageUrl())
                .status("PROCESSED")
                .build();
        trashEventRepository.save(event);

        // 통 적재량 업데이트
        if (request.getFillPercent() != null) {
            BinStatus status = binStatusRepository.findByBinId(bin.getId()).orElse(null);
            if (status != null) {
                status.setFillPercent(request.getFillPercent());
                status.setIsFull(request.getFillPercent() >= 100);
                binStatusRepository.save(status);
            }
        }
    }

    public List<TrashEventResponse> getAllEvents() {
        return trashEventRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(TrashEventResponse::new)
                .collect(Collectors.toList());
    }

    public TrashEventResponse getEvent(Long id) {
        return trashEventRepository.findById(id)
                .map(TrashEventResponse::new)
                .orElseThrow(() -> new IllegalArgumentException("이벤트를 찾을 수 없습니다."));
    }
}
