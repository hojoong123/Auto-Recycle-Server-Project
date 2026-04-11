package com.capstone.recycle.repository;

import com.capstone.recycle.entity.TrashEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TrashEventRepository extends JpaRepository<TrashEvent, Long> {

    List<TrashEvent> findByDeviceIdOrderByCreatedAtDesc(Long deviceId);

    List<TrashEvent> findAllByOrderByCreatedAtDesc();

    List<TrashEvent> findByIsDefectiveTrueOrderByCreatedAtDesc();
}
