package com.infiniteVision.schoolProject.modules.payment.service;

import com.infiniteVision.schoolProject.modules.payment.dto.request.AdjustLateFeeRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.request.GenerateLedgerRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.request.RegenerateLedgerRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.FeeLedgerDetailResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.FeeLedgerSummaryResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.GenerateLedgerResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.RegenerateLedgerResponseDTO;

/**
 * Student fee ledger generation and recalculation from fee structure master data.
 */
public interface FeeLedgerService {

    /**
     * Creates ledger rows for a student from active fee structures (skips existing rows).
     *
     * @param request student, academic year, optional term filter
     * @return created and skipped ledger summaries
     */
    GenerateLedgerResponseDTO generateLedgers(GenerateLedgerRequestDTO request);

    /**
     * Recalculates unpaid ledger amounts from current fee structure and scholarship discounts.
     */
    RegenerateLedgerResponseDTO regenerateLedgers(RegenerateLedgerRequestDTO request);

    /**
     * Sets late fee on an unpaid ledger and syncs the linked invoice when present.
     */
    FeeLedgerSummaryResponseDTO adjustLateFee(Long ledgerId, AdjustLateFeeRequestDTO request);

    /**
     * Loads a fee ledger with amounts and pending scholarship visibility.
     */
    FeeLedgerDetailResponseDTO getLedgerById(Long ledgerId);
}
