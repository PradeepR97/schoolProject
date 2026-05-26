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
 * Persistence access for {@link ClassMaster} records (grade per academic year).
 */
@Repository
public interface ClassMasterRepository extends JpaRepository<ClassMaster, Long> {

    Optional<ClassMaster> findByIdAndDeletedFalse(Long id);

    @Query(
            """
            SELECT c FROM ClassMaster c
            JOIN FETCH c.academicYear
            WHERE c.id = :id AND c.deleted = false
            """)
    Optional<ClassMaster> findByIdAndDeletedFalseWithAcademicYear(@Param("id") Long id);

    @Query(
            """
            SELECT c FROM ClassMaster c
            JOIN FETCH c.academicYear
            WHERE c.id IN :ids AND c.deleted = false
            """)
    List<ClassMaster> findAllByIdInAndDeletedFalseWithAcademicYear(@Param("ids") Collection<Long> ids);

    List<ClassMaster> findAllByIdInAndDeletedFalse(Collection<Long> ids);

    List<ClassMaster> findAllByDeletedFalseOrderByClassNameAsc();

    @Query(
            """
            SELECT c FROM ClassMaster c
            JOIN FETCH c.academicYear
            WHERE c.deleted = false
            ORDER BY c.className ASC
            """)
    List<ClassMaster> findAllWithAcademicYearByDeletedFalseOrderByClassNameAsc();

    List<ClassMaster> findAllByAcademicYear_IdAndDeletedFalseOrderByClassNameAsc(Long academicYearId);

    @Query(
            """
            SELECT c FROM ClassMaster c
            JOIN FETCH c.academicYear
            WHERE c.deleted = false AND c.academicYear.id = :academicYearId
            ORDER BY c.className ASC
            """)
    List<ClassMaster> findAllWithAcademicYearByAcademicYearIdAndDeletedFalseOrderByClassNameAsc(
            @Param("academicYearId") Long academicYearId);

    boolean existsByClassNameAndAcademicYear_IdAndDeletedFalse(String className, Long academicYearId);

    boolean existsByClassNameAndAcademicYear_IdAndIdNotAndDeletedFalse(
            String className, Long academicYearId, Long classId);
}
