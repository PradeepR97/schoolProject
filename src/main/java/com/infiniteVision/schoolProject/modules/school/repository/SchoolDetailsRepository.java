package com.infiniteVision.schoolProject.modules.school.repository;

import com.infiniteVision.schoolProject.modules.school.entity.SchoolDetails;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Persistence access for {@link SchoolDetails}.
 */
@Repository
public interface SchoolDetailsRepository extends JpaRepository<SchoolDetails, Long> {

    Optional<SchoolDetails> findByIdAndDeletedFalse(Long id);

    Page<SchoolDetails> findAllByDeletedFalse(Pageable pageable);

    Page<SchoolDetails> findAllByActiveTrueAndDeletedFalse(Pageable pageable);

    boolean existsBySchoolCodeAndDeletedFalse(String schoolCode);

    boolean existsBySchoolCodeAndIdNotAndDeletedFalse(String schoolCode, Long id);

    boolean existsByUdiseCodeAndDeletedFalse(String udiseCode);

    boolean existsByUdiseCodeAndIdNotAndDeletedFalse(String udiseCode, Long id);

    boolean existsByAffiliationNoAndDeletedFalse(String affiliationNo);

    boolean existsByAffiliationNoAndIdNotAndDeletedFalse(String affiliationNo, Long id);
}
