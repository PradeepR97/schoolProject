package com.infiniteVision.schoolProject.modules.student.repository;

import com.infiniteVision.schoolProject.modules.student.entity.StudentDocument;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Persistence access for {@link StudentDocument} records.
 */
@Repository
public interface StudentDocumentRepository extends JpaRepository<StudentDocument, Long> {

    Optional<StudentDocument> findByStudent_IdAndDeletedFalse(Long studentId);

    boolean existsByStudent_IdAndDeletedFalse(Long studentId);

    boolean existsByAadharNoAndDeletedFalse(String aadharNo);

    boolean existsByAadharNoAndIdNotAndDeletedFalse(String aadharNo, Long docId);
}
