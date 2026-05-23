package com.infiniteVision.schoolProject.modules.academic.repository;

import com.infiniteVision.schoolProject.modules.academic.entity.ClassMaster;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Persistence access for {@link ClassMaster} records.
 */
@Repository
public interface ClassMasterRepository extends JpaRepository<ClassMaster, Long> {

    Optional<ClassMaster> findByIdAndDeletedFalse(Long id);

    @Query(
            """
            SELECT c FROM ClassMaster c
            LEFT JOIN FETCH c.section
            WHERE c.id IN :ids AND c.deleted = false
            """)
    List<ClassMaster> findAllByIdInAndDeletedFalseWithSection(@Param("ids") Collection<Long> ids);

    List<ClassMaster> findAllByIdInAndDeletedFalse(Collection<Long> ids);

    List<ClassMaster> findAllByDeletedFalseOrderByClassNameAscSection_DisplayOrderAscSection_SectionCodeAsc();

    List<ClassMaster> findAllByAcademicYear_IdAndDeletedFalseOrderByClassNameAscSection_DisplayOrderAscSection_SectionCodeAsc(
            Long academicYearId);

    boolean existsByClassNameAndSection_IdAndAcademicYear_IdAndDeletedFalse(
            String className, Long sectionId, Long academicYearId);

    boolean existsByClassNameAndSection_IdAndAcademicYear_IdAndIdNotAndDeletedFalse(
            String className, Long sectionId, Long academicYearId, Long classId);
}
