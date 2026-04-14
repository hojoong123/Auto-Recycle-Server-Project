package com.capstone.recycle.Repository;

import com.capstone.recycle.Entity.BinStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BinStatusRepository extends JpaRepository<BinStatus, Long> {

    Optional<BinStatus> findByBinId(Long binId);
}
