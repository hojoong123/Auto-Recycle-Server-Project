package com.capstone.recycle.service;

import com.capstone.recycle.dto.response.DeviceResponse;
import com.capstone.recycle.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public List<DeviceResponse> getAllDevices() {
        return deviceRepository.findAll()
                .stream()
                .map(DeviceResponse::new)
                .collect(Collectors.toList());
    }

    public DeviceResponse getDevice(Long id) {
        return deviceRepository.findById(id)
                .map(DeviceResponse::new)
                .orElseThrow(() -> new IllegalArgumentException("장치를 찾을 수 없습니다."));
    }
}
