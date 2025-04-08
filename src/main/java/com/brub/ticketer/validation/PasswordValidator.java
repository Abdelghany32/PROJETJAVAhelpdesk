package com.brub.ticketer.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PasswordValidator {
    // Minimum password requirements
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 64;

    public static ValidationResult validate(String password) {
        List<String> errors = new ArrayList<>();

        // Check null or empty
        if (password == null || password.trim().isEmpty()) {
            errors.add("Password cannot be empty");
            return new ValidationResult(false, errors);
        }

        // Check length
        if (password.length() < MIN_LENGTH) {
            errors.add("Password must be at least " + MIN_LENGTH + " characters long");
        }
        if (password.length() > MAX_LENGTH) {
            errors.add("Password must not exceed " + MAX_LENGTH + " characters");
        }

        // Check complexity requirements
        if (!hasUppercaseLetter(password)) {
            errors.add("Password must contain at least one uppercase letter");
        }
        if (!hasLowercaseLetter(password)) {
            errors.add("Password must contain at least one lowercase letter");
        }
        if (!hasDigit(password)) {
            errors.add("Password must contain at least one digit");
        }
        if (!hasSpecialCharacter(password)) {
            errors.add("Password must contain at least one special character (!@#$%^&*()_+)");
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    private static boolean hasUppercaseLetter(String password) {
        return Pattern.compile("[A-Z]").matcher(password).find();
    }

    private static boolean hasLowercaseLetter(String password) {
        return Pattern.compile("[a-z]").matcher(password).find();
    }

    private static boolean hasDigit(String password) {
        return Pattern.compile("\\d").matcher(password).find();
    }

    private static boolean hasSpecialCharacter(String password) {
        return Pattern.compile("[!@#$%^&*()_+]").matcher(password).find();
    }

    // Inner class to return validation result
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return errors;
        }
    }
}