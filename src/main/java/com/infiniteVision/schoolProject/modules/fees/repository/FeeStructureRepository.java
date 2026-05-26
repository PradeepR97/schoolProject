package com.infiniteVision.schoolProject.modules.fees.repository;

import com.infiniteVision.schoolProject.modules.fees.entity.FeeStructure;
import com.infiniteVision.schoolProject.modules.fees.enums.TermType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Persistence access for {@link FeeStructure} rows (amount per grade, year, fee type, term).
 */
@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {

    Optional<FeeStructure> findByIdAndDeletedFalse(Long id);

    @Query(
            """
            SELECT fs FROM FeeStructure fs
            JOIN FETCH fs.academicYear
            JOIN FETCH fs.classMaster cm
            JOIN FETCH fs.feeType
            WHERE fs.id = :id AND fs.deleted = false
            """)
    Optional<FeeStructure> findActiveWithRelationsById(@Param("id") Long id);

    @Query(
            value = """
                    SELECT fs FROM FeeStructure fs
                    JOIN FETCH fs.academicYear ay
                    JOIN FETCH fs.classMaster cm
                    JOIN FETCH fs.feeType ft
                    WHERE fs.deleted = false
                    AND (:academicYearId IS NULL OR ay.id = :academicYearId)
                    AND (:classId IS NULL OR cm.id = :classId)
                    AND (:feeTypeId IS NULL OR ft.id = :feeTypeId)
                    AND (:activeOnly = false OR fs.active = true)
                    """,
            countQuery = """
                    SELECT COUNT(fs) FROM FeeStructure fs
                    WHERE fs.deleted = false
                    AND (:academicYearId IS NULL OR fs.academicYear.id = :academicYearId)
                    AND (:classId IS NULL OR fs.classMaster.id = :classId)
                    AND (:feeTypeId IS NULL OR fs.feeType.id = :feeTypeId)
                    AND (:activeOnly = false OR fs.active = true)
                    """)
    Page<FeeStructure> findAllFiltered(
            @Param("academicYearId") Long academicYearId,
            @Param("classId") Long classId,
            @Param("feeTypeId") Long feeTypeId,
            @Param("activeOnly") boolean activeOnly,
            Pageable pageable);

    boolean existsByAcademicYear_IdAndClassMaster_IdAndFeeType_IdAndTermTypeAndDeletedFalse(
            Long academicYearId, Long classId, Long feeTypeId, TermType termType);

    boolean existsByAcademicYear_IdAndClassMaster_IdAndFeeType_IdAndTermTypeAndIdNotAndDeletedFalse(
            Long academicYearId, Long classId, Long feeTypeId, TermType termType, Long structureId);

    @Query(
            """
            SELECT fs FROM FeeStructure fs
            JOIN FETCH fs.feeType
            JOIN FETCH fs.academicYear
            JOIN FETCH fs.classMaster
            WHERE fs.deleted = false
            AND fs.active = true
            AND fs.classMaster.id = :classId
            AND fs.academicYear.id = :academicYearId
            """)
    List<FeeStructure> findActiveByClassIdAndAcademicYearId(
            @Param("classId") Long classId, @Param("academicYearId") Long academicYearId);
}
