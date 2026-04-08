package com.capstone.recycle.Service;

import com.capstone.recycle.DTO.response.ErrorLogResponse;
import com.capstone.recycle.Entity.BinErrorLog;
import com.capstone.recycle.Entity.DeviceErrorLog;
import com.capstone.recycle.Repository.BinErrorLogRepository;
import com.capstone.recycle.Repository.DeviceErrorLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ErrorLogService {

    private final BinErrorLogRepository binErrorLogRepository;
    private final DeviceErrorLogRepository deviceErrorLogRepository;

    // 전체 오류 (미해결 상단, 해결됨 하단 정렬)
    public List<ErrorLogResponse> getAllErrors() {
        List<ErrorLogResponse> list = new ArrayList<>();
        binErrorLogRepository.findAll().stream()
                .map(ErrorLogResponse::new).forEach(list::add);
        deviceErrorLogRepository.findAll().stream()
                .map(ErrorLogResponse::new).forEach(list::add);

        // 미해결 먼저, 그 다음 최신순
        list.sort(Comparator.comparing(ErrorLogResponse::getResolved)
                .thenComparing(Comparator.comparing(ErrorLogResponse::getCreatedAt).reversed()));
        return list;
    }

    // 통 오류 해결 처리
    public void resolveBinError(Long id) {
        BinErrorLog log = binErrorLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("오류 로그를 찾을 수 없습니다."));
        log.setResolved(true);
        log.setResolvedAt(LocalDateTime.now());
        binErrorLogRepository.save(log);
    }

    // 장치 오류 해결 처리
    public void resolveDeviceError(Long id) {
        DeviceErrorLog log = deviceErrorLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("오류 로그를 찾을 수 없습니다."));
        log.setResolved(true);
        log.setResolvedAt(LocalDateTime.now());
        deviceErrorLogRepository.save(log);
    }
}
