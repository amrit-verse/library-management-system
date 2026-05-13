package exception;

/**
 * InvalidUserException - Custom checked exception.
 *
 * OOP Concept: Exception Handling / Custom Exceptions
 *
 * Thrown in scenarios such as:
 *   - Login attempt with wrong username or password
 *   - Registration with an already-taken username/email
 *   - User trying to perform admin-only actions
 *   - User not found in the database
 */
public class InvalidUserException extends Exception {

    /** Serial version UID for serialization. */
    private static final long serialVersionUID = 2L;

    /** The username involved in the exception (for context). */
    private String username;

    // ─── Constructors ──────────────────────────────────────────────────────────

    /**
     * Constructor with a message only.
     *
     * @param message description of the error
     */
    public InvalidUserException(String message) {
        super(message);
    }

    /**
     * Constructor with the offending username and a message.
     *
     * @param username the login name that caused the issue
     * @param message  description of the error
     */
    public InvalidUserException(String username, String message) {
        super(message);
        this.username = username;
    }

    /**
     * Constructor that wraps an underlying cause.
     *
     * @param message description of the error
     * @param cause   the original exception
     */
    public InvalidUserException(String message, Throwable cause) {
        super(message, cause);
    }

    // ─── Getter ────────────────────────────────────────────────────────────────

    /**
     * Returns the username that triggered this exception.
     *
     * @return username or null if not set
     */
    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        String base = "InvalidUserException: " + getMessage();
        return (username != null) ? base + " [Username: " + username + "]" : base;
    }
}
