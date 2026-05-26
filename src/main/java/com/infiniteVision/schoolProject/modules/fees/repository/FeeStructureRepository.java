package com.infiniteVision.schoolProject.modules.fees.repository;

import com.infiniteVision.schoolProject.modules.fees.entity.FeeStructure;
import com.infiniteVision.schoolProject.modules.fees.enums.TermType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Persistence access for {@link FeeStructure} rows (amount per grade, section, year, head, term).
 */
@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {

    Optional<FeeStructure> findByIdAndDeletedFalse(Long id);

    @Query(
            """
            SELECT fs FROM FeeStructure fs
            JOIN FETCH fs.academicYear
            JOIN FETCH fs.classMaster cm
            JOIN FETCH fs.section
            JOIN FETCH fs.feeHead
            WHERE fs.id = :id AND fs.deleted = false
            """)
    Optional<FeeStructure> findActiveWithRelationsById(@Param("id") Long id);

    @Query(
            value = """
                    SELECT fs FROM FeeStructure fs
                    JOIN FETCH fs.academicYear ay
                    JOIN FETCH fs.classMaster cm
                    JOIN FETCH fs.section sec
                    JOIN FETCH fs.feeHead fh
                    WHERE fs.deleted = false
                    AND (:academicYearId IS NULL OR ay.id = :academicYearId)
                    AND (:classId IS NULL OR cm.id = :classId)
                    AND (:sectionId IS NULL OR sec.id = :sectionId)
                    AND (:feeHeadId IS NULL OR fh.id = :feeHeadId)
                    AND (:activeOnly = false OR fs.active = true)
                    """,
            countQuery = """
                    SELECT COUNT(fs) FROM FeeStructure fs
                    WHERE fs.deleted = false
                    AND (:academicYearId IS NULL OR fs.academicYear.id = :academicYearId)
                    AND (:classId IS NULL OR fs.classMaster.id = :classId)
                    AND (:sectionId IS NULL OR fs.section.id = :sectionId)
                    AND (:feeHeadId IS NULL OR fs.feeHead.id = :feeHeadId)
                    AND (:activeOnly = false OR fs.active = true)
                    """)
    Page<FeeStructure> findAllFiltered(
            @Param("academicYearId") Long academicYearId,
            @Param("classId") Long classId,
            @Param("sectionId") Long sectionId,
            @Param("feeHeadId") Long feeHeadId,
            @Param("activeOnly") boolean activeOnly,
            Pageable pageable);

    boolean existsByAcademicYear_IdAndClassMaster_IdAndSection_IdAndFeeHead_IdAndTermTypeAndDeletedFalse(
            Long academicYearId, Long classId, Long sectionId, Long feeHeadId, TermType termType);

    boolean existsByAcademicYear_IdAndClassMaster_IdAndSection_IdAndFeeHead_IdAndTermTypeAndIdNotAndDeletedFalse(
            Long academicYearId,
            Long classId,
            Long sectionId,
            Long feeHeadId,
            TermType termType,
            Long structureId);

    @Query(
            """
            SELECT fs FROM FeeStructure fs
            JOIN FETCH fs.feeHead
            JOIN FETCH fs.academicYear
            JOIN FETCH fs.classMaster
            JOIN FETCH fs.section
            WHERE fs.deleted = false
            AND fs.active = true
            AND fs.classMaster.id = :classId
            AND fs.section.id = :sectionId
            AND fs.academicYear.id = :academicYearId
            """)
    java.util.List<FeeStructure> findActiveByClassIdAndSectionIdAndAcademicYearId(
            @Param("classId") Long classId,
            @Param("sectionId") Long sectionId,
            @Param("academicYearId") Long academicYearId);
}
