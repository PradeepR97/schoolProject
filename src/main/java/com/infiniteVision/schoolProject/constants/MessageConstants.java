package com.infiniteVision.schoolProject.constants;

/**
 * Centralized API and error messages. Do not hardcode these strings in handlers or controllers.
 */
public final class MessageConstants {

    private MessageConstants() {
    }

    public static final String VALIDATION_FAILED = "Validation failed";
    public static final String INVALID_REQUEST = "Invalid request";
    public static final String RESOURCE_NOT_FOUND = "Resource not found";
    public static final String UNAUTHORIZED = "Unauthorized";
    public static final String ACCESS_DENIED = "Access denied";
    public static final String AUTHENTICATION_FAILED = "Authentication failed";
    public static final String LOGIN_SUCCESS = "Login successful";
    public static final String LOGOUT_SUCCESS = "Logout successful";
    public static final String PASSWORD_CHANGED_SUCCESS = "Password changed successfully";
    public static final String CURRENT_PASSWORD_INCORRECT = "Current password is incorrect";
    public static final String NEW_PASSWORD_MISMATCH = "New password and confirmation do not match";
    public static final String NEW_PASSWORD_SAME_AS_CURRENT = "New password must be different from current password";
    public static final String USER_CREATED_SUCCESS = "User created successfully";
    public static final String USERS_LISTED_SUCCESS = "Users retrieved successfully";
    public static final String USER_RETRIEVED_SUCCESS = "User retrieved successfully";
    public static final String USER_UPDATED_SUCCESS = "User updated successfully";
    public static final String USER_DELETED_SUCCESS = "User deleted successfully";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_UPDATE_EMPTY = "At least one field must be provided to update";
    public static final String USER_DELETE_SELF_FORBIDDEN = "You cannot delete your own account";
    public static final String LAST_ADMIN_CANNOT_DELETE = "Cannot delete the last active administrator";
    public static final String USER_LIST_FORBIDDEN = "You are not allowed to list users";
    public static final String USER_MANAGEMENT_FORBIDDEN = "You are not allowed to manage users";
    public static final String USER_CREATION_FORBIDDEN = "You are not allowed to create users";
    public static final String ROLE_ASSIGNMENT_FORBIDDEN = "You are not allowed to assign this role";
    public static final String USERNAME_ALREADY_EXISTS = "Username is already taken";
    public static final String EMAIL_ALREADY_EXISTS = "Email is already registered";
    public static final String PHONE_ALREADY_EXISTS = "Phone number is already registered";
    public static final String BUSINESS_ERROR = "Request could not be processed";
    public static final String DATA_INTEGRITY_VIOLATION = "Data conflict or constraint violation";
    public static final String DATABASE_ERROR = "Database operation failed";
    public static final String REDIS_UNAVAILABLE = "Cache service temporarily unavailable";
    public static final String INTERNAL_SERVER_ERROR = "An unexpected error occurred";
    public static final String STUDENTS_LISTED_SUCCESS = "Students retrieved successfully";
    public static final String STUDENT_RETRIEVED_SUCCESS = "Student retrieved successfully";
    public static final String STUDENT_UPDATED_SUCCESS = "Student updated successfully";
    public static final String STUDENT_DELETED_SUCCESS = "Student deleted successfully";
    public static final String STUDENT_NOT_FOUND = "Student not found";
    public static final String STUDENT_UPDATE_EMPTY = "At least one field must be provided to update";
    public static final String STUDENT_ADMITTED_SUCCESS = "Student admitted successfully";
    public static final String ADMISSION_NO_ALREADY_EXISTS = "Admission number already exists";
    public static final String STUDENT_AADHAR_ALREADY_EXISTS = "Student Aadhar number is already registered";
    public static final String DOCUMENT_AADHAR_ALREADY_EXISTS = "Document Aadhar number is already registered";
    public static final String AADHAR_MISMATCH = "Student Aadhar and document Aadhar must match";
    public static final String CLASS_NOT_FOUND = "Class not found";
    public static final String ACADEMIC_YEAR_NOT_FOUND = "Academic year not found";
    public static final String CLASS_ACADEMIC_YEAR_MISMATCH =
            "Class does not belong to the specified academic year";
    public static final String GUARDIAN_DETAILS_REQUIRED =
            "Guardian name and phone are required when primary contact is guardian";
    public static final String PARENT_CONTACT_REQUIRED =
            "At least one of father, mother, or guardian name is required";

    public static final String MASTER_DATA_RETRIEVED_SUCCESS = "Master data retrieved successfully";

    public static final String SCHOLARSHIP_CREATED_SUCCESS = "Scholarship created successfully";
    public static final String SCHOLARSHIPS_LISTED_SUCCESS = "Scholarships retrieved successfully";
    public static final String SCHOLARSHIP_RETRIEVED_SUCCESS = "Scholarship retrieved successfully";
    public static final String SCHOLARSHIP_UPDATED_SUCCESS = "Scholarship updated successfully";
    public static final String SCHOLARSHIP_DELETED_SUCCESS = "Scholarship deleted successfully";
    public static final String SCHOLARSHIP_NOT_FOUND = "Scholarship not found";
    public static final String SCHOLARSHIP_UPDATE_EMPTY = "At least one field must be provided to update";
    public static final String FEE_HEAD_NOT_FOUND = "Fee head not found";
    public static final String FEE_HEAD_REQUIRED_FOR_SPECIFIC_HEAD =
            "Fee head is required when applicable to is SPECIFIC_HEAD";
    public static final String DISCOUNT_PERCENTAGE_EXCEEDS_100 =
            "Percentage discount cannot exceed 100";

    public static final String SCHOLARSHIP_APPLICATION_CREATED_SUCCESS =
            "Scholarship application created successfully";
    public static final String SCHOLARSHIP_APPLICATIONS_LISTED_SUCCESS =
            "Scholarship applications retrieved successfully";
    public static final String SCHOLARSHIP_APPLICATION_RETRIEVED_SUCCESS =
            "Scholarship application retrieved successfully";
    public static final String SCHOLARSHIP_APPLICATION_APPROVED_SUCCESS =
            "Scholarship application approved successfully";
    public static final String SCHOLARSHIP_APPLICATION_REJECTED_SUCCESS =
            "Scholarship application rejected successfully";
    public static final String SCHOLARSHIP_APPLICATION_BULK_APPROVED_SUCCESS =
            "Scholarship applications bulk approved successfully";
    public static final String SCHOLARSHIP_APPLICATION_BULK_REJECTED_SUCCESS =
            "Scholarship applications bulk rejected successfully";
    public static final String SCHOLARSHIP_APPLICATION_NOT_FOUND = "Scholarship application not found";
    public static final String SCHOLARSHIP_APPLICATION_ALREADY_EXISTS =
            "Scholarship application already exists for this student, scheme, and academic year";
    public static final String SCHOLARSHIP_APPLICATION_ALREADY_APPROVED =
            "Scholarship application is already approved";
    public static final String SCHOLARSHIP_APPLICATION_ALREADY_REJECTED =
            "Scholarship application is already rejected";
    public static final String SCHOLARSHIP_APPLICATION_INVALID_STATE =
            "Scholarship application is not in a valid state for this action";
    public static final String SCHOLARSHIP_PRINCIPAL_ALREADY_APPROVED =
            "Principal has already approved this application";
    public static final String SCHOLARSHIP_CORRESPONDENT_ALREADY_APPROVED =
            "Correspondent has already approved this application";
    public static final String SCHOLARSHIP_AWAITING_PRINCIPAL_APPROVAL =
            "Application is awaiting principal approval";
    public static final String SCHOLARSHIP_APPROVAL_ROLE_FORBIDDEN =
            "You are not allowed to perform this approval action";
    public static final String SCHOLARSHIP_SCHEME_YEAR_MISMATCH =
            "Scholarship scheme does not belong to the specified academic year";

    public static final String FEE_STRUCTURES_LISTED_SUCCESS = "Fee structures retrieved successfully";
    public static final String FEE_STRUCTURE_RETRIEVED_SUCCESS = "Fee structure retrieved successfully";
    public static final String FEE_STRUCTURE_CREATED_SUCCESS = "Fee structure created successfully";
    public static final String FEE_STRUCTURE_UPDATED_SUCCESS = "Fee structure updated successfully";
    public static final String FEE_STRUCTURE_DELETED_SUCCESS = "Fee structure deleted successfully";
    public static final String FEE_STRUCTURE_NOT_FOUND = "Fee structure not found";
    public static final String FEE_STRUCTURE_UPDATE_EMPTY = "At least one field must be provided to update";
    public static final String FEE_STRUCTURE_ALREADY_EXISTS =
            "Fee structure already exists for this academic year, class, fee head, and term";

    public static final String PAYMENTS_LISTED_SUCCESS = "Payments retrieved successfully";
    public static final String INVOICES_LISTED_SUCCESS = "Invoices retrieved successfully";
    public static final String INVOICE_RETRIEVED_SUCCESS = "Invoice retrieved successfully";
    public static final String INVOICE_GENERATED_SUCCESS = "Invoice generated successfully";
    public static final String INVOICE_NOT_FOUND = "Invoice not found";
    public static final String INVOICE_ALREADY_EXISTS = "Invoice already exists for this ledger";
    public static final String FEE_LEDGER_GENERATED_SUCCESS = "Fee ledgers generated successfully";
    public static final String FEE_LEDGER_REGENERATED_SUCCESS = "Fee ledgers regenerated successfully";
    public static final String FEE_LEDGER_RETRIEVED_SUCCESS = "Fee ledger retrieved successfully";
    public static final String FEE_LEDGER_NOT_FOUND = "Fee ledger not found";
    public static final String FEE_LEDGER_ALREADY_PAID = "Fee ledger is already fully paid";
    public static final String FEE_LEDGER_CANNOT_ADJUST_LATE_FEE =
            "Late fee cannot be adjusted on a fully paid ledger";
    public static final String FEE_LEDGER_PAID_EXCEEDS_NET = "Paid amount cannot exceed net ledger amount";
    public static final String NO_FEE_STRUCTURES_FOR_CLASS =
            "No active fee structures found for the student's class and academic year";
    public static final String LATE_FEE_ADJUSTED_SUCCESS = "Late fee adjusted successfully";
    public static final String PAYMENT_COLLECTED_SUCCESS = "Payment collected successfully";
    public static final String PAYMENT_RECEIPT_RETRIEVED_SUCCESS = "Payment receipt retrieved successfully";
    public static final String PAYMENT_CANCELLED_SUCCESS = "Payment cancelled successfully";
    public static final String PAYMENT_REFUNDED_SUCCESS = "Payment refunded successfully";
    public static final String PAYMENT_NOT_FOUND = "Payment not found";
    public static final String STUDENT_DUES_RETRIEVED_SUCCESS = "Student fee dues retrieved successfully";
    public static final String STUDENT_CLASS_NOT_ASSIGNED = "Student does not have a class assigned";
    public static final String STUDENT_LEDGER_MISMATCH = "Ledger does not belong to the specified student";
    public static final String PAYMENT_AMOUNT_EXCEEDS_BALANCE = "Payment amount exceeds ledger balance";
    public static final String PAYMENT_ALREADY_CANCELLED = "Payment is already cancelled";
    public static final String PAYMENT_ALREADY_REFUNDED = "Payment is already refunded";
    public static final String PAYMENT_CANNOT_REVERSE = "Payment cannot be reversed in its current state";
}
