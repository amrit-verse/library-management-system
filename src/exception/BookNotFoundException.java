package exception;

/**
 * BookNotFoundException - Custom checked exception.
 *
 * OOP Concept: Exception Handling / Custom Exceptions
 *
 * Thrown when a requested book does not exist in the library database,
 * e.g., searching/borrowing/updating/deleting a non-existent book ID.
 */
public class BookNotFoundException extends Exception {

    /** Serial version UID for serialization. */
    private static final long serialVersionUID = 1L;

    /** The book ID that could not be found. */
    private String bookId;

    // ─── Constructors ──────────────────────────────────────────────────────────

    /**
     * Constructor with a simple message.
     *
     * @param message description of the error
     */
    public BookNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor with the missing book ID and a message.
     *
     * @param bookId  the ID that was searched for
     * @param message description of the error
     */
    public BookNotFoundException(String bookId, String message) {
        super(message);
        this.bookId = bookId;
    }

    /**
     * Constructor that wraps an underlying cause.
     *
     * @param message description of the error
     * @param cause   the original exception
     */
    public BookNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    // ─── Getter ────────────────────────────────────────────────────────────────

    /**
     * Returns the book ID that triggered this exception.
     *
     * @return bookId or null if not set
     */
    public String getBookId() {
        return bookId;
    }

    @Override
    public String toString() {
        String base = "BookNotFoundException: " + getMessage();
        return (bookId != null) ? base + " [Book ID: " + bookId + "]" : base;
    }
}
