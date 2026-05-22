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
}
