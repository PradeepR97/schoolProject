package com.infiniteVision.schoolProject.modules.payment.service;

import com.infiniteVision.schoolProject.modules.payment.dto.request.GenerateLedgerRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.GenerateLedgerResponseDTO;

/**
 * Student fee ledger generation from {@code fee_structure} master data.
 */
public interface FeeLedgerService {

    /**
     * Creates ledger rows for a student from active fee structures (skips existing rows).
     *
     * @param request student, academic year, optional term filter
     * @return created and skipped ledger summaries
     */
    GenerateLedgerResponseDTO generateLedgers(GenerateLedgerRequestDTO request);
}
