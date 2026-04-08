package com.capstone.recycle.Service;

import com.capstone.recycle.DTO.response.DeviceResponse;
import com.capstone.recycle.Repository.DeviceRepository;
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
