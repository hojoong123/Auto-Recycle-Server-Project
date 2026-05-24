package com.capstone.recycle.Repository;

import com.capstone.recycle.Entity.InspectionNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InspectionNotificationRepository
        extends JpaRepository<InspectionNotification, Long> {

    List<InspectionNotification> findByReceiverIdOrderBySentAtDesc(Long receiverId);

    long countByReceiverIdAndStatus(Long receiverId, String status);
}
