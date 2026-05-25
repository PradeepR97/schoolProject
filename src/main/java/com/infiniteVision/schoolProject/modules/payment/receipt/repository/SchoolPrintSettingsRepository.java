package com.infiniteVision.schoolProject.modules.payment.receipt.repository;

import com.infiniteVision.schoolProject.modules.payment.receipt.entity.SchoolPrintSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolPrintSettingsRepository extends JpaRepository<SchoolPrintSettings, Long> {
}
