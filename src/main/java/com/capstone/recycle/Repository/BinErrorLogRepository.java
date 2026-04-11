package com.capstone.recycle.repository;

import com.capstone.recycle.entity.BinErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BinErrorLogRepository extends JpaRepository<BinErrorLog, Long> {

    List<BinErrorLog> findByBinIdOrderByCreatedAtDesc(Long binId);

    List<BinErrorLog> findByResolvedFalseOrderByCreatedAtDesc();
}
