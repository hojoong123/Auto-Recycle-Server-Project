package com.capstone.recycle.Repository;

import com.capstone.recycle.Entity.TrashType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TrashTypeRepository extends JpaRepository<TrashType, Long> {

    Optional<TrashType> findByTypeCode(String typeCode);
}
