package com.capstone.recycle.Repository;

import com.capstone.recycle.Entity.Bin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BinRepository extends JpaRepository<Bin, Long> {

    List<Bin> findByDeviceId(Long deviceId);

    Optional<Bin> findByBinCode(String binCode);
}
