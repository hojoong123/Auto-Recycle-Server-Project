package com.capstone.recycle.Service;

import com.capstone.recycle.DTO.request.TrashEventRequest;
import com.capstone.recycle.DTO.response.ErrorLogResponse;
import com.capstone.recycle.DTO.response.TrashEventResponse;
import com.capstone.recycle.Entity.*;
import com.capstone.recycle.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final BinErrorLogRepository binErrorLogRepository;
    private final WebSocketService webSocketService;

    @Transactional
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

        // ✅ 분류 이벤트 실시간 브로드캐스트
        TrashEventResponse trashEventResponse = new TrashEventResponse(event);
        //webSocketService.broadcastTrashEvent(trashEventResponse);

        // 통 적재량 업데이트
        if (request.getFillPercent() != null) {
            BinStatus status = binStatusRepository.findByBinId(bin.getId()).orElse(null);
            if (status != null) {
                status.setFillPercent(request.getFillPercent());
                status.setIsFull(request.getFillPercent() >= 100);
                binStatusRepository.save(status);

                // ✅ 적재량 변경 실시간 브로드캐스트
                //webSocketService.broadcastBinStatus(device.getId(), status);

                // ✅ 적재량 80% 이상이면 경고 로그 + 실시간 알림
                if (request.getFillPercent() >= 80) {
                    BinErrorLog errorLog = BinErrorLog.builder()
                            .bin(bin)
                            .errorType("CAPACITY_WARNING")
                            .message(bin.getBinCode() + " 적재량 "
                                    + request.getFillPercent() + "% - 수거 필요")
                            .resolved(false)
                            .build();
                    binErrorLogRepository.save(errorLog);

                    //webSocketService.broadcastError(new ErrorLogResponse(errorLog));
                }
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

    public void delete(Long id) {
        trashEventRepository.deleteById(id);
    }
}
