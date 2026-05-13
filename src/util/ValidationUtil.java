package util;

import java.util.regex.Pattern;

/**
 * ValidationUtil - Static utility class for input validation.
 *
 * OOP Concept: Abstraction (hides regex and validation logic)
 *
 * All methods are static — no need to instantiate this class.
 * Used throughout the application to validate user inputs before
 * passing them to service/database layers.
 */
public class ValidationUtil {

    /** Private constructor — utility class should not be instantiated. */
    private ValidationUtil() {}

    // ─── Regex Patterns ────────────────────────────────────────────────────────

    /** Email pattern: basic RFC-compliant check. */
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[\\w.+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");

    /** Phone pattern: 10-digit Indian mobile number. */
    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^[6-9]\\d{9}$");

    /** ISBN-13 pattern (digits and hyphens). */
    private static final Pattern ISBN_PATTERN =
        Pattern.compile("^[0-9]{3}-[0-9]{10}$|^[0-9]{13}$|^[0-9]{3}-[0-9]{1}-[0-9]{3}-[0-9]{5}-[0-9]{1}$");

    /** Book ID pattern: 'BK' followed by 3 digits, e.g. BK001. */
    private static final Pattern BOOK_ID_PATTERN =
        Pattern.compile("^BK\\d{3,5}$");

    // ─── String Validators ─────────────────────────────────────────────────────

    /**
     * Checks if a string is not null and not blank.
     *
     * @param  value  the string to check
     * @return true if the string has content
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Checks if a string meets a minimum length requirement.
     *
     * @param  value  string to check
     * @param  minLen minimum length
     * @return true if length ≥ minLen
     */
    public static boolean hasMinLength(String value, int minLen) {
        return isNotEmpty(value) && value.trim().length() >= minLen;
    }

    /**
     * Checks if a string does not exceed a maximum length.
     *
     * @param  value  string to check
     * @param  maxLen maximum allowed length
     * @return true if length ≤ maxLen
     */
    public static boolean hasMaxLength(String value, int maxLen) {
        return value != null && value.length() <= maxLen;
    }

    // ─── Email Validator ───────────────────────────────────────────────────────

    /**
     * Validates an email address against a regex pattern.
     *
     * @param  email  email string to validate
     * @return true if the email format is valid
     */
    public static boolean isValidEmail(String email) {
        return isNotEmpty(email) && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    // ─── Phone Validator ───────────────────────────────────────────────────────

    /**
     * Validates a 10-digit Indian mobile number.
     *
     * @param  phone  phone string to validate
     * @return true if valid Indian mobile number
     */
    public static boolean isValidPhone(String phone) {
        return isNotEmpty(phone) && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    // ─── ISBN Validator ────────────────────────────────────────────────────────

    /**
     * Validates an ISBN-13 number (with or without hyphens).
     *
     * @param  isbn  ISBN string to validate
     * @return true if valid ISBN-13 format
     */
    public static boolean isValidIsbn(String isbn) {
        return isNotEmpty(isbn) && ISBN_PATTERN.matcher(isbn.trim()).matches();
    }

    // ─── Book ID Validator ─────────────────────────────────────────────────────

    /**
     * Validates a Book ID (must start with 'BK' followed by digits).
     *
     * @param  bookId  book ID to validate
     * @return true if format is BKxxx
     */
    public static boolean isValidBookId(String bookId) {
        return isNotEmpty(bookId) && BOOK_ID_PATTERN.matcher(bookId.trim().toUpperCase()).matches();
    }

    // ─── Numeric Validators ────────────────────────────────────────────────────

    /**
     * Checks if a value is a positive integer (> 0).
     *
     * @param  value  integer to check
     * @return true if value > 0
     */
    public static boolean isPositive(int value) {
        return value > 0;
    }

    /**
     * Checks if a string can be parsed as a positive integer.
     *
     * @param  str  string representation of a number
     * @return true if it can be parsed and is > 0
     */
    public static boolean isPositiveNumber(String str) {
        try {
            return Integer.parseInt(str.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ─── Password Validator ────────────────────────────────────────────────────

    /**
     * Validates a password: minimum 6 characters.
     *
     * @param  password  password string
     * @return true if password meets requirements
     */
    public static boolean isValidPassword(String password) {
        return hasMinLength(password, 6);
    }

    // ─── Username Validator ────────────────────────────────────────────────────

    /**
     * Validates a username: 3–20 characters, alphanumeric and underscores only.
     *
     * @param  username  username to validate
     * @return true if valid
     */
    public static boolean isValidUsername(String username) {
        if (!hasMinLength(username, 3) || !hasMaxLength(username, 20)) return false;
        return username.trim().matches("^[a-zA-Z0-9_]+$");
    }

    // ─── Display ───────────────────────────────────────────────────────────────

    /** Prints a separator line for UI formatting. */
    public static void printSeparator() {
        System.out.println("─".repeat(90));
    }

    /** Prints a double separator line. */
    public static void printDoubleSeparator() {
        System.out.println("═".repeat(90));
    }

    /** Prints a section header. */
    public static void printHeader(String title) {
        printDoubleSeparator();
        System.out.printf("%40s%n", title);
        printDoubleSeparator();
    }
}
