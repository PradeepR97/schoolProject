package com.infiniteVision.schoolProject.modules.payment.repository;

import com.infiniteVision.schoolProject.modules.payment.entity.StudentFeeLedger;
import com.infiniteVision.schoolProject.modules.payment.enums.FeeBillingTerm;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Persistence access for {@link StudentFeeLedger} records.
 */
@Repository
public interface StudentFeeLedgerRepository extends JpaRepository<StudentFeeLedger, Long> {

    Optional<StudentFeeLedger> findByIdAndDeletedFalse(Long id);

    List<StudentFeeLedger> findAllByStudent_IdAndDeletedFalseOrderByDueDateAsc(Long studentId);

    List<StudentFeeLedger> findAllByStudent_IdAndAcademicYear_IdAndDeletedFalseOrderByDueDateAsc(
            Long studentId, Long academicYearId);

    boolean existsByStudent_IdAndAcademicYear_IdAndTermAndFeeStructure_IdAndDeletedFalse(
            Long studentId, Long academicYearId, FeeBillingTerm term, Long structureId);
}
