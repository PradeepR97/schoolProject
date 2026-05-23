package com.infiniteVision.schoolProject.modules.fees.repository;

import com.infiniteVision.schoolProject.modules.fees.entity.FeeStructure;
import com.infiniteVision.schoolProject.modules.fees.enums.TermType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Persistence access for {@link FeeStructure} rows (amount per class, year, head, term).
 */
@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {

    Optional<FeeStructure> findByIdAndDeletedFalse(Long id);

    List<FeeStructure> findAllByAcademicYear_IdAndClassMaster_IdAndDeletedFalseAndActiveTrue(
            Long academicYearId, Long classId);

    List<FeeStructure> findAllByAcademicYear_IdAndClassMaster_IdAndFeeHead_IdAndDeletedFalse(
            Long academicYearId, Long classId, Long feeHeadId);

    boolean existsByAcademicYear_IdAndClassMaster_IdAndFeeHead_IdAndTermTypeAndDeletedFalse(
            Long academicYearId, Long classId, Long feeHeadId, TermType termType);

    boolean existsByAcademicYear_IdAndClassMaster_IdAndFeeHead_IdAndTermTypeAndIdNotAndDeletedFalse(
            Long academicYearId, Long classId, Long feeHeadId, TermType termType, Long structureId);
}
