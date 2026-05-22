package com.infiniteVision.schoolProject.modules.academic.repository;

import com.infiniteVision.schoolProject.modules.academic.entity.ClassMaster;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Persistence access for {@link ClassMaster} records.
 */
@Repository
public interface ClassMasterRepository extends JpaRepository<ClassMaster, Long> {

    Optional<ClassMaster> findByIdAndDeletedFalse(Long id);

    List<ClassMaster> findAllByDeletedFalseOrderByClassNameAscSectionAsc();

    List<ClassMaster> findAllByAcademicYear_IdAndDeletedFalseOrderByClassNameAscSectionAsc(Long academicYearId);

    boolean existsByClassNameAndSectionAndAcademicYear_IdAndDeletedFalse(
            String className, String section, Long academicYearId);

    boolean existsByClassNameAndSectionAndAcademicYear_IdAndIdNotAndDeletedFalse(
            String className, String section, Long academicYearId, Long classId);
}
