package com.infiniteVision.schoolProject.modules.auth.util;

import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.modules.auth.dto.request.ForgotPasswordRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.request.LoginSendOtpRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.request.LoginVerifyOtpRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.request.VerifyOtpRequestDTO;
import java.util.List;

/**
 * Normalizes and validates email-or-phone identifiers for public auth flows.
 */
public final class AuthIdentifierUtil {

    private AuthIdentifierUtil() {
    }

    public static void validateForgotPasswordIdentifiers(ForgotPasswordRequestDTO request) {
        validateExactlyOneIdentifier(hasEmail(request.getEmail()), hasPhone(request.getPhone()));
    }

    public static void validateVerifyOtpIdentifiers(VerifyOtpRequestDTO request) {
        validateExactlyOneIdentifier(hasEmail(request.getEmail()), hasPhone(request.getPhone()));
    }

    public static void validateLoginSendOtpIdentifiers(LoginSendOtpRequestDTO request) {
        validateExactlyOneIdentifier(hasEmail(request.getEmail()), hasPhone(request.getPhone()));
    }

    public static void validateLoginVerifyOtpIdentifiers(LoginVerifyOtpRequestDTO request) {
        validateExactlyOneIdentifier(hasEmail(request.getEmail()), hasPhone(request.getPhone()));
    }

    /**
     * Stable Redis key segment: lowercase email or trimmed phone.
     */
    public static String normalizeIdentifier(String email, String phone) {
        if (hasEmail(email)) {
            return "email:" + email.trim().toLowerCase();
        }
        return "phone:" + phone.trim();
    }

    public static boolean hasEmail(String email) {
        return email != null && !email.isBlank();
    }

    public static boolean hasPhone(String phone) {
        return phone != null && !phone.isBlank();
    }

    private static void validateExactlyOneIdentifier(boolean hasEmail, boolean hasPhone) {
        if (hasEmail == hasPhone) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED,
                    List.of("Provide exactly one of email or phone"));
        }
    }
}
