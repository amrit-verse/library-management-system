package model;

/**
 * Book - Model class representing a library book.
 *
 * OOP Concepts:
 *   - Encapsulation  : all fields are private; accessed via getters/setters
 *   - Abstraction    : hides internal representation from outside classes
 */
public class Book {

    // ─── Fields ────────────────────────────────────────────────────────────────
    private String bookId;       // Unique identifier  e.g. BK001
    private String bookName;     // Title of the book
    private String authorName;   // Author's full name
    private String category;     // Genre / subject area
    private int    quantity;     // Copies currently available
    private String isbn;         // International Standard Book Number

    // ─── Constructors ──────────────────────────────────────────────────────────

    /** Default (no-arg) constructor. */
    public Book() {}

    /**
     * Full constructor — used when retrieving a book from the database.
     *
     * @param bookId     unique book identifier
     * @param bookName   title of the book
     * @param authorName author's name
     * @param category   category / genre
     * @param quantity   copies available
     * @param isbn       ISBN number
     */
    public Book(String bookId, String bookName, String authorName,
                String category, int quantity, String isbn) {
        this.bookId     = bookId;
        this.bookName   = bookName;
        this.authorName = authorName;
        this.category   = category;
        this.quantity   = quantity;
        this.isbn       = isbn;
    }

    // ─── Getters ───────────────────────────────────────────────────────────────

    public String getBookId()     { return bookId;     }
    public String getBookName()   { return bookName;   }
    public String getAuthorName() { return authorName; }
    public String getCategory()   { return category;   }
    public int    getQuantity()   { return quantity;   }
    public String getIsbn()       { return isbn;       }

    // ─── Setters ───────────────────────────────────────────────────────────────

    public void setBookId(String bookId)         { this.bookId     = bookId;     }
    public void setBookName(String bookName)     { this.bookName   = bookName;   }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public void setCategory(String category)     { this.category   = category;   }
    public void setIsbn(String isbn)             { this.isbn       = isbn;       }

    /**
     * Sets quantity — rejects negative values.
     * @param quantity number of available copies
     */
    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        this.quantity = quantity;
    }

    // ─── toString ──────────────────────────────────────────────────────────────

    /**
     * Returns a formatted single-line representation of the book.
     * Used when printing search results or book lists.
     */
    @Override
    public String toString() {
        return String.format(
            "| %-6s | %-35s | %-20s | %-12s | %-4d | %-15s |",
            bookId, bookName, authorName, category, quantity, isbn
        );
    }

    /** Returns a detailed multi-line description of the book. */
    public String toDetailString() {
        return  "\n  Book ID   : " + bookId     +
                "\n  Title     : " + bookName   +
                "\n  Author    : " + authorName +
                "\n  Category  : " + category   +
                "\n  Available : " + quantity   +
                "\n  ISBN      : " + isbn;
    }
}
