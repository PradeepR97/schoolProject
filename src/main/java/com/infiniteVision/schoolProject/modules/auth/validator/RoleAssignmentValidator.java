package com.infiniteVision.schoolProject.modules.auth.validator;

import com.infiniteVision.schoolProject.modules.auth.enums.UserRole;
import org.springframework.stereotype.Component;

/**
 * Defines which roles an authenticated user may assign when creating a new user.
 */
@Component
public class RoleAssignmentValidator {

    /**
     * Returns true if {@code creatorRole} is allowed to create a user with {@code targetRole}.
     */
    public boolean canAssignRole(UserRole creatorRole, UserRole targetRole) {
        if (creatorRole == null || targetRole == null) {
            return false;
        }
        return switch (creatorRole) {
            case ADMIN -> true;
            case PRINCIPAL -> UserRole.CORRESPONDENT.equals(targetRole) || UserRole.ACCOUNTANT.equals(targetRole);
            case CORRESPONDENT, ACCOUNTANT -> false;
        };
    }
}
