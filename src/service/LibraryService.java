package service;

import database.DBConnection;
import exception.BookNotFoundException;
import model.Book;
import model.Transaction;
import util.FileUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * LibraryService - Core service class handling all book and transaction operations.
 *
 * OOP Concepts:
 *   - Abstraction    : callers only call methods; SQL is fully hidden
 *   - Encapsulation  : all DB interaction in private/public methods
 *   - Exception Handling: throws custom exceptions for business-rule violations
 *   - Collections   : returns List<Book> and List<Transaction>
 *
 * Responsibilities:
 *   - Add / Update / Delete / View / Search books
 *   - Borrow a book (creates a transaction, reduces quantity)
 *   - Return a book (updates transaction, calculates fine, restores quantity)
 *   - View borrow history for a user
 *   - Generate unique Book IDs automatically
 */
public class LibraryService {

    // ══════════════════════════════════════════════════════════════════════════
    //  BOOK MANAGEMENT
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Adds a new book to the library.
     * Generates a unique Book ID automatically if not provided.
     *
     * @param  book  Book object containing details to insert
     * @return true if book was added successfully
     * @throws SQLException on database error
     */
    public boolean addBook(Book book) throws SQLException {

        // Auto-generate Book ID if not set
        if (book.getBookId() == null || book.getBookId().isEmpty()) {
            book.setBookId(generateNextBookId());
        }

        // Check for duplicate ISBN
        if (isbnExists(book.getIsbn())) {
            System.out.println("[ERROR] A book with ISBN " + book.getIsbn() + " already exists.");
            return false;
        }

        String sql = "INSERT INTO books (book_id, book_name, author_name, category, quantity, isbn) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, book.getBookId());
            ps.setString(2, book.getBookName());
            ps.setString(3, book.getAuthorName());
            ps.setString(4, book.getCategory());
            ps.setInt   (5, book.getQuantity());
            ps.setString(6, book.getIsbn());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                FileUtil.logAdminAction("ADMIN", "ADD_BOOK",
                    "ID=" + book.getBookId() + ", Title=" + book.getBookName());
                return true;
            }
        }
        return false;
    }

    // ─── Update Book ───────────────────────────────────────────────────────────

    /**
     * Updates the details of an existing book.
     *
     * @param  book  Book object with updated values (book_id must match existing record)
     * @return true if update was successful
     * @throws BookNotFoundException if no book with the given ID exists
     * @throws SQLException          on database error
     */
    public boolean updateBook(Book book) throws BookNotFoundException, SQLException {

        if (!bookExists(book.getBookId())) {
            throw new BookNotFoundException(book.getBookId(),
                "Cannot update. Book not found with ID: " + book.getBookId());
        }

        String sql = "UPDATE books SET book_name = ?, author_name = ?, category = ?, " +
                     "quantity = ?, isbn = ? WHERE book_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, book.getBookName());
            ps.setString(2, book.getAuthorName());
            ps.setString(3, book.getCategory());
            ps.setInt   (4, book.getQuantity());
            ps.setString(5, book.getIsbn());
            ps.setString(6, book.getBookId());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                FileUtil.logAdminAction("ADMIN", "UPDATE_BOOK", "ID=" + book.getBookId());
                return true;
            }
        }
        return false;
    }

    // ─── Delete Book ───────────────────────────────────────────────────────────

    /**
     * Deletes a book from the library.
     * Prevents deletion if there are active borrows for this book.
     *
     * @param  bookId  the ID of the book to delete
     * @return true if deletion was successful
     * @throws BookNotFoundException if the book does not exist
     * @throws SQLException          on database error
     */
    public boolean deleteBook(String bookId) throws BookNotFoundException, SQLException {

        if (!bookExists(bookId)) {
            throw new BookNotFoundException(bookId,
                "Cannot delete. Book not found with ID: " + bookId);
        }

        // Prevent deletion if book is currently borrowed
        if (hasActiveBorrows(bookId)) {
            System.out.println("[ERROR] Book '" + bookId +
                "' has active borrows. Return all copies before deleting.");
            return false;
        }

        String sql = "DELETE FROM books WHERE book_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bookId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                FileUtil.logAdminAction("ADMIN", "DELETE_BOOK", "ID=" + bookId);
                return true;
            }
        }
        return false;
    }

    // ─── Get Book By ID ────────────────────────────────────────────────────────

    /**
     * Retrieves a single book by its book ID.
     *
     * @param  bookId  the book ID to look up
     * @return Book object
     * @throws BookNotFoundException if the book is not in the database
     * @throws SQLException          on database error
     */
    public Book getBookById(String bookId) throws BookNotFoundException, SQLException {
        String sql = "SELECT * FROM books WHERE book_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBook(rs);
                }
            }
        }
        throw new BookNotFoundException(bookId, "Book not found with ID: " + bookId);
    }

    // ─── Get All Books ─────────────────────────────────────────────────────────

    /**
     * Retrieves all books in the library, ordered by book_id.
     *
     * @return List of Book objects
     * @throws SQLException on database error
     */
    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY book_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                books.add(mapBook(rs));
            }
        }
        return books;
    }

    // ─── Search Books ──────────────────────────────────────────────────────────

    /**
     * Searches books by title or author name (case-insensitive, partial match).
     *
     * @param  keyword  search term
     * @return List of matching Book objects
     * @throws SQLException on database error
     */
    public List<Book> searchBooks(String keyword) throws SQLException {
        List<Book> results = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE " +
                     "LOWER(book_name) LIKE ? OR LOWER(author_name) LIKE ? OR " +
                     "LOWER(category) LIKE ? OR book_id LIKE ? OR isbn LIKE ?";

        String likeKeyword = "%" + keyword.toLowerCase() + "%";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 1; i <= 5; i++) {
                ps.setString(i, likeKeyword);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapBook(rs));
                }
            }
        }
        return results;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  BORROW / RETURN OPERATIONS
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Borrows a book for a user.
     *
     * Steps:
     *  1. Verify book exists and quantity > 0
     *  2. Check user has not already borrowed the same book
     *  3. Insert a BORROWED transaction
     *  4. Decrement book quantity by 1
     *
     * @param  bookId  the book to borrow
     * @param  userId  the user borrowing the book
     * @return true if borrow was successful
     * @throws BookNotFoundException if the book does not exist
     * @throws SQLException          on database error
     */
    public boolean borrowBook(String bookId, int userId)
            throws BookNotFoundException, SQLException {

        Book book = getBookById(bookId);   // throws if not found

        if (book.getQuantity() <= 0) {
            System.out.println("[ERROR] Sorry, '" + book.getBookName() + "' is currently not available.");
            return false;
        }

        if (alreadyBorrowed(bookId, userId)) {
            System.out.println("[ERROR] You have already borrowed this book and not yet returned it.");
            return false;
        }

        Connection conn = DBConnection.getConnection();
        conn.setAutoCommit(false);  // Begin transaction

        try {
            // 1. Insert transaction record
            String insertTxn = "INSERT INTO transactions (book_id, user_id, borrow_date, due_date, status) " +
                                "VALUES (?, ?, ?, ?, 'BORROWED')";
            LocalDate today   = LocalDate.now();
            LocalDate dueDate = today.plusDays(Transaction.BORROW_DAYS);

            try (PreparedStatement ps = conn.prepareStatement(insertTxn)) {
                ps.setString(1, bookId);
                ps.setInt   (2, userId);
                ps.setDate  (3, Date.valueOf(today));
                ps.setDate  (4, Date.valueOf(dueDate));
                ps.executeUpdate();
            }

            // 2. Decrease book quantity
            String updateQty = "UPDATE books SET quantity = quantity - 1 WHERE book_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateQty)) {
                ps.setString(1, bookId);
                ps.executeUpdate();
            }

            conn.commit();  // Commit both operations together
            FileUtil.logTransaction("BORROW", "UID:" + userId, bookId);
            System.out.println("[SUCCESS] Book borrowed! Due date: " + dueDate);
            return true;

        } catch (SQLException e) {
            conn.rollback();  // Rollback on any failure
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // ─── Return Book ───────────────────────────────────────────────────────────

    /**
     * Returns a borrowed book.
     *
     * Steps:
     *  1. Find the active BORROWED transaction for this user + book
     *  2. Calculate fine if overdue
     *  3. Update transaction with return_date, fine_amount, status = RETURNED
     *  4. Increment book quantity by 1
     *
     * @param  bookId  the book to return
     * @param  userId  the user returning the book
     * @return the fine amount in rupees (0 if no fine)
     * @throws BookNotFoundException if no active borrow record found
     * @throws SQLException          on database error
     */
    public double returnBook(String bookId, int userId)
            throws BookNotFoundException, SQLException {

        // Find the active transaction
        String findTxn =
            "SELECT * FROM transactions WHERE book_id = ? AND user_id = ? AND status = 'BORROWED'";

        int    transactionId;
        LocalDate dueDate;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(findTxn)) {

            ps.setString(1, bookId);
            ps.setInt   (2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new BookNotFoundException(bookId,
                        "No active borrow record found for Book ID: " + bookId +
                        " and your account.");
                }
                transactionId = rs.getInt("transaction_id");
                dueDate       = rs.getDate("due_date").toLocalDate();
            }
        }

        // Calculate fine
        LocalDate returnDate = LocalDate.now();
        Transaction temp = new Transaction();
        temp.setDueDate(dueDate);
        double fine = temp.calculateFine(returnDate);

        Connection conn = DBConnection.getConnection();
        conn.setAutoCommit(false);

        try {
            // 1. Update transaction
            String updateTxn =
                "UPDATE transactions SET return_date = ?, fine_amount = ?, status = 'RETURNED' " +
                "WHERE transaction_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateTxn)) {
                ps.setDate  (1, Date.valueOf(returnDate));
                ps.setDouble(2, fine);
                ps.setInt   (3, transactionId);
                ps.executeUpdate();
            }

            // 2. Increase book quantity
            String updateQty = "UPDATE books SET quantity = quantity + 1 WHERE book_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateQty)) {
                ps.setString(1, bookId);
                ps.executeUpdate();
            }

            conn.commit();
            FileUtil.logTransaction("RETURN", "UID:" + userId, bookId);
            return fine;

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // ─── Borrow History ────────────────────────────────────────────────────────

    /**
     * Returns the full borrow history for a specific user,
     * including book name and author from a JOIN query.
     *
     * @param  userId  the user whose history to retrieve
     * @return List of formatted history strings
     * @throws SQLException on database error
     */
    public List<String> getBorrowHistory(int userId) throws SQLException {
        List<String> history = new ArrayList<>();

        String sql =
            "SELECT t.transaction_id, b.book_name, b.author_name, " +
            "       t.borrow_date, t.due_date, t.return_date, " +
            "       t.fine_amount, t.status " +
            "FROM transactions t " +
            "JOIN books b ON t.book_id = b.book_id " +
            "WHERE t.user_id = ? " +
            "ORDER BY t.borrow_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String returnDate = rs.getString("return_date");
                    String entry = String.format(
                        "| %-4d | %-35s | %-12s | %-12s | %-12s | Rs.%-7.2f | %-9s |",
                        rs.getInt   ("transaction_id"),
                        rs.getString("book_name"),
                        rs.getDate  ("borrow_date"),
                        rs.getDate  ("due_date"),
                        (returnDate != null ? returnDate : "Pending"),
                        rs.getDouble("fine_amount"),
                        rs.getString("status")
                    );
                    history.add(entry);
                }
            }
        }
        return history;
    }

    // ─── All Transactions (Admin) ──────────────────────────────────────────────

    /**
     * Returns all transactions in the system (admin view).
     *
     * @return List of formatted transaction strings
     * @throws SQLException on database error
     */
    public List<String> getAllTransactions() throws SQLException {
        List<String> transactions = new ArrayList<>();

        String sql =
            "SELECT t.transaction_id, u.username, b.book_name, " +
            "       t.borrow_date, t.due_date, t.return_date, " +
            "       t.fine_amount, t.status " +
            "FROM transactions t " +
            "JOIN books b ON t.book_id  = b.book_id " +
            "JOIN users u ON t.user_id  = u.user_id " +
            "ORDER BY t.transaction_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String returnDate = rs.getString("return_date");
                String entry = String.format(
                    "| %-4d | %-15s | %-30s | %-12s | %-12s | %-12s | Rs.%-7.2f | %-9s |",
                    rs.getInt   ("transaction_id"),
                    rs.getString("username"),
                    rs.getString("book_name"),
                    rs.getDate  ("borrow_date"),
                    rs.getDate  ("due_date"),
                    (returnDate != null ? returnDate : "Pending"),
                    rs.getDouble("fine_amount"),
                    rs.getString("status")
                );
                transactions.add(entry);
            }
        }
        return transactions;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PRIVATE HELPER METHODS
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Generates the next available Book ID by finding the highest existing ID.
     * Format: BK001, BK002, ... BK999
     *
     * @return next unique Book ID string
     * @throws SQLException on database error
     */
    private String generateNextBookId() throws SQLException {
        String sql = "SELECT book_id FROM books ORDER BY book_id DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString("book_id");         // e.g. "BK010"
                int number = Integer.parseInt(lastId.substring(2)); // extract 10
                return String.format("BK%03d", number + 1);         // → "BK011"
            }
        }
        return "BK001"; // First book
    }

    /**
     * Checks whether a book with the given ID exists in the database.
     *
     * @param  bookId  the book ID to check
     * @return true if found
     */
    private boolean bookExists(String bookId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM books WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Checks whether an ISBN already exists in the database.
     *
     * @param  isbn  the ISBN to check
     * @return true if found
     */
    private boolean isbnExists(String isbn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM books WHERE isbn = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Checks if a book has any active (unreturned) borrows.
     *
     * @param  bookId  the book ID to check
     * @return true if there are active borrows
     */
    private boolean hasActiveBorrows(String bookId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM transactions WHERE book_id = ? AND status = 'BORROWED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Checks if a user has already borrowed a specific book and not returned it yet.
     *
     * @param  bookId  the book ID
     * @param  userId  the user ID
     * @return true if an unreturned borrow exists
     */
    private boolean alreadyBorrowed(String bookId, int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM transactions " +
                     "WHERE book_id = ? AND user_id = ? AND status = 'BORROWED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bookId);
            ps.setInt   (2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Maps a ResultSet row to a Book object.
     * Extracted to avoid code duplication across multiple query methods.
     *
     * @param  rs  current ResultSet row
     * @return Book object
     */
    private Book mapBook(ResultSet rs) throws SQLException {
        return new Book(
            rs.getString("book_id"),
            rs.getString("book_name"),
            rs.getString("author_name"),
            rs.getString("category"),
            rs.getInt   ("quantity"),
            rs.getString("isbn")
        );
    }
}
