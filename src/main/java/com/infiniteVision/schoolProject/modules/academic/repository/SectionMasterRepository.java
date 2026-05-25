package com.infiniteVision.schoolProject.modules.academic.repository;

import com.infiniteVision.schoolProject.modules.academic.entity.SectionMaster;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Persistence access for {@link SectionMaster} records.
 */
@Repository
public interface SectionMasterRepository extends JpaRepository<SectionMaster, Long> {

    Optional<SectionMaster> findByIdAndDeletedFalse(Long id);

    Optional<SectionMaster> findBySectionCodeAndDeletedFalse(String sectionCode);

    boolean existsBySectionCodeAndDeletedFalse(String sectionCode);

    List<SectionMaster> findAllByDeletedFalseOrderByDisplayOrderAscSectionCodeAsc();

    List<SectionMaster> findAllByActiveTrueAndDeletedFalseOrderByDisplayOrderAscSectionCodeAsc();
}
