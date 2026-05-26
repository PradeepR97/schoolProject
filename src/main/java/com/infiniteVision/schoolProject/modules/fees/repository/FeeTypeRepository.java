package com.infiniteVision.schoolProject.modules.fees.repository;

import com.infiniteVision.schoolProject.modules.fees.entity.FeeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Persistence access for {@link FeeType} master records.
 */
@Repository
public interface FeeTypeRepository extends JpaRepository<FeeType, Long> {

    Optional<FeeType> findByIdAndDeletedFalse(Long id);

    Optional<FeeType> findByFeeTypeCodeAndDeletedFalse(String feeTypeCode);

    boolean existsByFeeTypeCodeAndDeletedFalse(String feeTypeCode);

    List<FeeType> findAllByDeletedFalseOrderByDisplayOrderAscFeeTypeNameAsc();

    List<FeeType> findAllByActiveTrueAndDeletedFalseOrderByDisplayOrderAscFeeTypeNameAsc();
}
