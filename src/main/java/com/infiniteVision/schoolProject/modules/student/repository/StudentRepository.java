package com.infiniteVision.schoolProject.modules.student.repository;

import com.infiniteVision.schoolProject.modules.student.entity.Student;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Persistence access for {@link Student} records.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByAdmissionNoAndDeletedFalse(String admissionNo);

    boolean existsByAdmissionNoAndDeletedFalse(String admissionNo);

    boolean existsByApplicationNumberAndDeletedFalse(String applicationNumber);

    boolean existsByAadharNumberAndDeletedFalse(String aadharNumber);

    boolean existsByStudentIdCardNoAndDeletedFalse(String studentIdCardNo);

    Optional<Student> findByIdAndDeletedFalse(Long id);

    /**
     * Active students with parent row eagerly loaded for list screens.
     */
    @Query(
            value = """
                    SELECT s FROM Student s
                    LEFT JOIN FETCH s.parents
                    WHERE s.deleted = false
                    """,
            countQuery = "SELECT COUNT(s) FROM Student s WHERE s.deleted = false")
    Page<Student> findAllActiveWithParents(Pageable pageable);
}
