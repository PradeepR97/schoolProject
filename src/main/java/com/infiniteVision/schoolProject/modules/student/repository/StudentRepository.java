package com.infiniteVision.schoolProject.modules.student.repository;

import com.infiniteVision.schoolProject.modules.student.entity.Student;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Persistence access for {@link Student} records.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByAdmissionNoAndDeletedFalse(String admissionNo);

    boolean existsByAdmissionNoAndDeletedFalse(String admissionNo);

    boolean existsByAadharNumberAndDeletedFalse(String aadharNumber);

    boolean existsByStudentIdCardNoAndDeletedFalse(String studentIdCardNo);
}
