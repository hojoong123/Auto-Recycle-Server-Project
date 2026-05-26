package com.capstone.recycle.Service;

import com.capstone.recycle.DTO.request.InspectionDoneDto;
import com.capstone.recycle.DTO.request.InspectionRequestDto;
import com.capstone.recycle.DTO.response.NotificationResponse;
import com.capstone.recycle.Entity.*;
import com.capstone.recycle.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final InspectionNotificationRepository notiRepo;
    private final AdminRepository adminRepo;
    private final DeviceRepository deviceRepo;
    private final BinRepository binRepo;
    private final WebSocketService webSocketService;

    /** 웹(총괄) → 해당 층 ADMIN 들에게 점검 요청 */
    @Transactional
    public void sendInspectionRequest(Long senderId, InspectionRequestDto req) {
        Admin sender = adminRepo.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("발신 관리자를 찾을 수 없습니다."));

        if (req.getFloor() == null)
            throw new IllegalArgumentException("점검 대상 층(floor)이 필요합니다.");

        List<Admin> receivers = adminRepo
                .findByRoleAndFloorAndIsActiveTrue("ADMIN", req.getFloor());
        if (receivers.isEmpty())
            throw new IllegalStateException(req.getFloor() + "층 담당 관리자가 없습니다.");

        Device device = req.getDeviceId() != null
                ? deviceRepo.findById(req.getDeviceId()).orElse(null) : null;
        Bin bin = req.getBinId() != null
                ? binRepo.findById(req.getBinId()).orElse(null) : null;

        String title = req.getFloor() + "층 점검 요청";
        String message = req.getMessage() != null ? req.getMessage()
                : req.getFloor() + "층 쓰레기통 점검이 필요합니다.";

        for (Admin r : receivers) {
            InspectionNotification n = InspectionNotification.builder()
                    .sender(sender)
                    .receiver(r)
                    .device(device)
                    .bin(bin)
                    .floor(req.getFloor())
                    .title(title)
                    .message(message)
                    .notificationType("INSPECTION_REQUEST")
                    .status("SENT")
                    .build();
            notiRepo.save(n);
            webSocketService.broadcastNotification(r.getId(), new NotificationResponse(n));
        }
    }

    /** 앱(현장) → 총괄(SUPER_ADMIN) 들에게 점검 완료 보고 */
    @Transactional
    public void sendInspectionDone(Long senderId, InspectionDoneDto req) {
        Admin sender = adminRepo.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("발신 관리자를 찾을 수 없습니다."));

        List<Admin> receivers = adminRepo.findByRoleAndIsActiveTrue("SUPER_ADMIN");
        if (receivers.isEmpty())
            throw new IllegalStateException("총괄 관리자가 없습니다.");

        Device device = req.getDeviceId() != null
                ? deviceRepo.findById(req.getDeviceId()).orElse(null) : null;
        Bin bin = req.getBinId() != null
                ? binRepo.findById(req.getBinId()).orElse(null) : null;

        Integer floor = req.getFloor() != null ? req.getFloor() : sender.getFloor();
        String title = (floor != null ? floor + "층 " : "") + "점검 완료";
        String message = req.getMessage() != null ? req.getMessage()
                : sender.getName() + "님이 점검을 완료했습니다."
                  + (bin != null ? " (" + bin.getBinCode() + ")" : "");

        for (Admin r : receivers) {
            InspectionNotification n = InspectionNotification.builder()
                    .sender(sender)
                    .receiver(r)
                    .device(device)
                    .bin(bin)
                    .floor(floor)
                    .title(title)
                    .message(message)
                    .notificationType("INSPECTION_COMPLETE")
                    .status("SENT")
                    .build();
            notiRepo.save(n);
            webSocketService.broadcastNotification(r.getId(), new NotificationResponse(n));
        }
    }

    public List<NotificationResponse> list(Long receiverId) {
        return notiRepo.findByReceiverIdOrderBySentAtDesc(receiverId)
                .stream().map(NotificationResponse::new).collect(Collectors.toList());
    }

    public long countUnread(Long receiverId) {
        return notiRepo.countByReceiverIdAndStatus(receiverId, "SENT");
    }

    @Transactional
    public void markRead(Long id) {
        InspectionNotification n = notiRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));
        if ("SENT".equals(n.getStatus())) {
            n.setStatus("READ");
            n.setReadAt(LocalDateTime.now());
        }
    }

    @Transactional
    public void confirm(Long id) {
        InspectionNotification n = notiRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));
        n.setStatus("CONFIRMED");
        n.setConfirmedAt(LocalDateTime.now());
    }
    /** 라즈베리파이 → 총괄 관리자에게 직원 호출 */
    @Transactional
    public void sendStaffCall(

            String deviceId,
            String message,
            Integer floor
    ) {

        // ==============================
        // SUPER_ADMIN 조회
        // ==============================
        List<Admin> superAdmins =
                adminRepo.findByRoleAndIsActiveTrue(
                        "SUPER_ADMIN"
                );

        // ==============================
        // 층 관리자 조회
        // ==============================
        List<Admin> floorAdmins =
                adminRepo.findByRoleAndFloorAndIsActiveTrue(
                        "ADMIN",
                        floor
                );

        // ==============================
        // 합치기
        // ==============================
        List<Admin> receivers = new ArrayList<>();


        receivers.addAll(superAdmins);

        receivers.addAll(floorAdmins);

        if (receivers.isEmpty()) {

            throw new IllegalStateException(
                    "수신 가능한 관리자가 없습니다."
            );
        }

        String title = floor + "층 직원 호출";

        for (Admin r : receivers) {

            InspectionNotification n =
                    InspectionNotification.builder()

                            .sender(r)
                            .receiver(r)

                            .floor(floor)

                            .title(title)

                            .message(message)

                            .notificationType("STAFF_CALL")

                            .status("SENT")

                            .build();

            notiRepo.save(n);

            webSocketService.broadcastNotification(
                    r.getId(),
                    new NotificationResponse(n)
            );
        }
    }
}
