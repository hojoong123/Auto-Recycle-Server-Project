package com.capstone.recycle.Repository;

import com.capstone.recycle.Entity.TrashEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TrashEventRepository extends JpaRepository<TrashEvent, Long> {

    List<TrashEvent> findByDeviceIdOrderByCreatedAtDesc(Long deviceId);

    List<TrashEvent> findAllByOrderByCreatedAtDesc();

    List<TrashEvent> findByIsDefectiveTrueOrderByCreatedAtDesc();
}
