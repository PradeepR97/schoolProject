package com.infiniteVision.schoolProject.modules.fees.repository;

import com.infiniteVision.schoolProject.modules.fees.entity.FeeHead;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Persistence access for {@link FeeHead} master records.
 */
@Repository
public interface FeeHeadRepository extends JpaRepository<FeeHead, Long> {

    Optional<FeeHead> findByIdAndDeletedFalse(Long id);

    Optional<FeeHead> findByFeeHeadCodeAndDeletedFalse(String feeHeadCode);

    boolean existsByFeeHeadCodeAndDeletedFalse(String feeHeadCode);

    List<FeeHead> findAllByDeletedFalseOrderByDisplayOrderAscFeeHeadNameAsc();

    List<FeeHead> findAllByActiveTrueAndDeletedFalseOrderByDisplayOrderAscFeeHeadNameAsc();
}
