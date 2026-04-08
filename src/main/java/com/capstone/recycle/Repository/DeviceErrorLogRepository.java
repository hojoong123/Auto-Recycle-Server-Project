package com.capstone.recycle.Repository;

import com.capstone.recycle.Entity.DeviceErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DeviceErrorLogRepository extends JpaRepository<DeviceErrorLog, Long> {

    List<DeviceErrorLog> findByDeviceIdOrderByCreatedAtDesc(Long deviceId);

    List<DeviceErrorLog> findByResolvedFalseOrderByCreatedAtDesc();
}
