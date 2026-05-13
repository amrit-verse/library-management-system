package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * FileUtil - Utility class for logging application events to a text file.
 *
 * OOP Concept: File Handling (java.io), Abstraction
 *
 * All logs are appended to "library_log.txt" in the project root.
 * Used to keep a persistent audit trail of admin actions and transactions.
 */
public class FileUtil {

    /** Path to the log file (relative to project root). */
    private static final String LOG_FILE = "library_log.txt";

    /** Formatter for timestamps in log entries. */
    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Private constructor — utility class should not be instantiated. */
    private FileUtil() {}

    // ─── Core Log Writer ───────────────────────────────────────────────────────

    /**
     * Appends a single log entry to the log file.
     * Creates the file if it does not exist.
     *
     * @param level   log level string, e.g. "INFO", "WARN", "ERROR"
     * @param message the message to log
     */
    public static void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String entry = String.format("[%s] [%s] %s%n", timestamp, level, message);

        // try-with-resources: auto-closes writer even on exception
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(entry);
        } catch (IOException e) {
            System.err.println("[FileUtil] Failed to write log: " + e.getMessage());
        }
    }

    // ─── Convenience Methods ───────────────────────────────────────────────────

    /**
     * Logs an informational message.
     *
     * @param message info message
     */
    public static void logInfo(String message) {
        log("INFO", message);
    }

    /**
     * Logs a warning message.
     *
     * @param message warning message
     */
    public static void logWarning(String message) {
        log("WARN", message);
    }

    /**
     * Logs an error message.
     *
     * @param message error message
     */
    public static void logError(String message) {
        log("ERROR", message);
    }

    /**
     * Logs a transaction event (borrow or return).
     *
     * @param action   "BORROW" or "RETURN"
     * @param username the user performing the action
     * @param bookId   the book involved
     */
    public static void logTransaction(String action, String username, String bookId) {
        String message = String.format("ACTION=%s | USER=%s | BOOK=%s", action, username, bookId);
        log("TRANSACTION", message);
    }

    /**
     * Logs an admin action such as adding or deleting a book.
     *
     * @param adminName admin performing the action
     * @param action    description of the action
     * @param detail    additional detail
     */
    public static void logAdminAction(String adminName, String action, String detail) {
        String message = String.format("ADMIN=%s | ACTION=%s | DETAIL=%s", adminName, action, detail);
        log("ADMIN", message);
    }

    /**
     * Writes a startup banner to the log when the application launches.
     */
    public static void logStartup() {
        log("SYSTEM", "=== Library Management System Started ===");
    }

    /**
     * Writes a shutdown entry to the log when the application exits.
     */
    public static void logShutdown() {
        log("SYSTEM", "=== Library Management System Stopped ===");
    }
}
