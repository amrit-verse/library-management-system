package main;

import database.DBConnection;
import exception.BookNotFoundException;
import exception.InvalidUserException;
import model.Book;
import model.User;
import service.LibraryService;
import service.UserService;
import util.FileUtil;
import util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Main - Entry point for the Library Management System.
 *
 * OOP Concepts demonstrated here:
 *   - Objects        : User, Book, Admin instances created and used
 *   - Polymorphism   : login() returns User or Admin; isAdmin() drives menu
 *   - Exception Handling: try-catch throughout with custom exceptions
 *   - Abstraction    : calls service methods; no raw SQL here
 *
 * Flow:
 *   Main Menu → Admin Login / User Login / Register / Exit
 *   Admin Menu → book management + view users + transactions
 *   User  Menu → search, borrow, return, history
 */
public class Main {

    /** Scanner shared across the whole application. */
    private static final Scanner sc = new Scanner(System.in);

    /** Service objects created once and reused. */
    private static final LibraryService libraryService = new LibraryService();
    private static final UserService    userService    = new UserService();

    // ═══════════════════════════════════════════════════════════════════════════
    //  APPLICATION ENTRY POINT
    // ═══════════════════════════════════════════════════════════════════════════

    public static void main(String[] args) {

        FileUtil.logStartup();
        System.out.println();
        ValidationUtil.printHeader("  LIBRARY MANAGEMENT SYSTEM  ");
        System.out.println("  Welcome! Please choose an option to get started.");

        boolean running = true;

        while (running) {
            printMainMenu();
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1 -> handleAdminLogin();
                case 2 -> handleUserLogin();
                case 3 -> handleUserRegister();
                case 4 -> {
                    running = false;
                    System.out.println("\n  Thank you for using Library Management System. Goodbye!");
                }
                default -> System.out.println("[!] Invalid choice. Please enter 1-4.");
            }
        }

        // Cleanup
        DBConnection.closeConnection();
        FileUtil.logShutdown();
        sc.close();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  MAIN MENU
    // ═══════════════════════════════════════════════════════════════════════════

    private static void printMainMenu() {
        System.out.println();
        ValidationUtil.printSeparator();
        System.out.println("  MAIN MENU");
        ValidationUtil.printSeparator();
        System.out.println("  1. Admin Login");
        System.out.println("  2. User Login");
        System.out.println("  3. Register as New User");
        System.out.println("  4. Exit");
        ValidationUtil.printSeparator();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  AUTH HANDLERS
    // ═══════════════════════════════════════════════════════════════════════════

    /** Handles admin login flow. */
    private static void handleAdminLogin() {
        System.out.println("\n--- ADMIN LOGIN ---");
        String username = readString("  Username : ");
        String password = readString("  Password : ");

        try {
            User user = userService.login(username, password);

            if (!user.isAdmin()) {
                System.out.println("[!] Access denied. This account does not have admin privileges.");
                return;
            }
            System.out.println("\n  Welcome, Admin " + user.getUsername() + "!");
            showAdminMenu(user);

        } catch (InvalidUserException e) {
            System.out.println("[ERROR] " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
        }
    }

    /** Handles user login flow. */
    private static void handleUserLogin() {
        System.out.println("\n--- USER LOGIN ---");
        String username = readString("  Username : ");
        String password = readString("  Password : ");

        try {
            User user = userService.login(username, password);
            System.out.println("\n  Welcome back, " + user.getUsername() + "!");
            showUserMenu(user);

        } catch (InvalidUserException e) {
            System.out.println("[ERROR] " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
        }
    }

    /** Handles new user registration. */
    private static void handleUserRegister() {
        System.out.println("\n--- NEW USER REGISTRATION ---");

        String username = readString("  Username (3-20 chars, alphanumeric): ");
        if (!ValidationUtil.isValidUsername(username)) {
            System.out.println("[ERROR] Invalid username. Use 3-20 alphanumeric characters or underscore.");
            return;
        }

        String password = readString("  Password (min 6 chars)             : ");
        if (!ValidationUtil.isValidPassword(password)) {
            System.out.println("[ERROR] Password must be at least 6 characters.");
            return;
        }

        String email = readString("  Email                               : ");
        if (!ValidationUtil.isValidEmail(email)) {
            System.out.println("[ERROR] Invalid email format.");
            return;
        }

        String phone = readString("  Phone (10 digits)                   : ");
        if (!ValidationUtil.isValidPhone(phone)) {
            System.out.println("[ERROR] Invalid phone number. Enter a 10-digit mobile number.");
            return;
        }

        User newUser = new User(username, password, email, phone);

        try {
            boolean success = userService.registerUser(newUser);
            if (success) {
                System.out.println("\n  [SUCCESS] Registration complete! You can now log in.");
            }
        } catch (InvalidUserException e) {
            System.out.println("[ERROR] " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  ADMIN MENU
    // ═══════════════════════════════════════════════════════════════════════════

    private static void showAdminMenu(User admin) {
        boolean loggedIn = true;

        while (loggedIn) {
            printAdminMenu();
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1  -> adminAddBook(admin);
                case 2  -> adminUpdateBook(admin);
                case 3  -> adminDeleteBook(admin);
                case 4  -> adminViewAllBooks();
                case 5  -> adminSearchBooks();
                case 6  -> adminViewAllUsers();
                case 7  -> adminDeleteUser(admin);
                case 8  -> adminViewAllTransactions();
                case 9  -> {
                    System.out.println("\n  Logged out. Goodbye, " + admin.getUsername() + "!");
                    loggedIn = false;
                }
                default -> System.out.println("[!] Invalid choice. Please enter 1-9.");
            }
        }
    }

    private static void printAdminMenu() {
        System.out.println();
        ValidationUtil.printSeparator();
        System.out.println("  ADMIN MENU");
        ValidationUtil.printSeparator();
        System.out.println("  1.  Add Book");
        System.out.println("  2.  Update Book");
        System.out.println("  3.  Delete Book");
        System.out.println("  4.  View All Books");
        System.out.println("  5.  Search Books");
        System.out.println("  6.  View All Users");
        System.out.println("  7.  Delete User");
        System.out.println("  8.  View All Transactions");
        System.out.println("  9.  Logout");
        ValidationUtil.printSeparator();
    }

    // ─── Admin: Add Book ───────────────────────────────────────────────────────

    private static void adminAddBook(User admin) {
        System.out.println("\n--- ADD NEW BOOK ---");

        String name = readString("  Book Title  : ");
        if (!ValidationUtil.isNotEmpty(name)) {
            System.out.println("[ERROR] Book title cannot be empty."); return;
        }

        String author = readString("  Author Name : ");
        if (!ValidationUtil.isNotEmpty(author)) {
            System.out.println("[ERROR] Author name cannot be empty."); return;
        }

        String category = readString("  Category    : ");
        if (!ValidationUtil.isNotEmpty(category)) {
            System.out.println("[ERROR] Category cannot be empty."); return;
        }

        String qtyStr = readString("  Quantity    : ");
        if (!ValidationUtil.isPositiveNumber(qtyStr)) {
            System.out.println("[ERROR] Quantity must be a positive number."); return;
        }

        String isbn = readString("  ISBN        : ");
        if (!ValidationUtil.isNotEmpty(isbn)) {
            System.out.println("[ERROR] ISBN cannot be empty."); return;
        }

        Book book = new Book();
        book.setBookName(name.trim());
        book.setAuthorName(author.trim());
        book.setCategory(category.trim());
        book.setQuantity(Integer.parseInt(qtyStr.trim()));
        book.setIsbn(isbn.trim());

        try {
            boolean success = libraryService.addBook(book);
            if (success) {
                System.out.println("\n  [SUCCESS] Book added with ID: " + book.getBookId());
                FileUtil.logAdminAction(admin.getUsername(), "ADD_BOOK", book.getBookName());
            }
        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
        }
    }

    // ─── Admin: Update Book ────────────────────────────────────────────────────

    private static void adminUpdateBook(User admin) {
        System.out.println("\n--- UPDATE BOOK ---");
        String bookId = readString("  Enter Book ID to update: ").toUpperCase().trim();

        try {
            Book existing = libraryService.getBookById(bookId);
            System.out.println("\n  Current details:" + existing.toDetailString());
            System.out.println("\n  Enter new values (press Enter to keep current):");

            String name     = readStringOptional("  New Title    [" + existing.getBookName()   + "]: ");
            String author   = readStringOptional("  New Author   [" + existing.getAuthorName() + "]: ");
            String category = readStringOptional("  New Category [" + existing.getCategory()   + "]: ");
            String qtyStr   = readStringOptional("  New Quantity [" + existing.getQuantity()   + "]: ");
            String isbn     = readStringOptional("  New ISBN     [" + existing.getIsbn()        + "]: ");

            // Keep existing values if blank input
            if (!name.isEmpty())     existing.setBookName(name.trim());
            if (!author.isEmpty())   existing.setAuthorName(author.trim());
            if (!category.isEmpty()) existing.setCategory(category.trim());
            if (!isbn.isEmpty())     existing.setIsbn(isbn.trim());
            if (!qtyStr.isEmpty()) {
                if (!ValidationUtil.isPositiveNumber(qtyStr)) {
                    System.out.println("[ERROR] Invalid quantity."); return;
                }
                existing.setQuantity(Integer.parseInt(qtyStr.trim()));
            }

            boolean success = libraryService.updateBook(existing);
            if (success) {
                System.out.println("\n  [SUCCESS] Book updated successfully.");
                FileUtil.logAdminAction(admin.getUsername(), "UPDATE_BOOK", bookId);
            }

        } catch (BookNotFoundException e) {
            System.out.println("[ERROR] " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
        }
    }

    // ─── Admin: Delete Book ────────────────────────────────────────────────────

    private static void adminDeleteBook(User admin) {
        System.out.println("\n--- DELETE BOOK ---");
        String bookId = readString("  Enter Book ID to delete: ").toUpperCase().trim();

        System.out.print("  Are you sure you want to delete " + bookId + "? (yes/no): ");
        String confirm = sc.nextLine().trim();

        if (!"yes".equalsIgnoreCase(confirm)) {
            System.out.println("  Deletion cancelled.");
            return;
        }

        try {
            boolean success = libraryService.deleteBook(bookId);
            if (success) {
                System.out.println("\n  [SUCCESS] Book " + bookId + " deleted.");
                FileUtil.logAdminAction(admin.getUsername(), "DELETE_BOOK", bookId);
            }
        } catch (BookNotFoundException e) {
            System.out.println("[ERROR] " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
        }
    }

    // ─── Admin: View All Books ─────────────────────────────────────────────────

    private static void adminViewAllBooks() {
        System.out.println("\n--- ALL BOOKS ---");
        try {
            List<Book> books = libraryService.getAllBooks();
            if (books.isEmpty()) {
                System.out.println("  No books found in the library.");
                return;
            }
            printBookTableHeader();
            books.forEach(b -> System.out.println(b));
            ValidationUtil.printSeparator();
            System.out.println("  Total books: " + books.size());
        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
        }
    }

    // ─── Admin: Search Books ───────────────────────────────────────────────────

    private static void adminSearchBooks() {
        System.out.println("\n--- SEARCH BOOKS ---");
        String keyword = readString("  Enter keyword (title / author / category / ISBN): ");

        try {
            List<Book> results = libraryService.searchBooks(keyword);
            if (results.isEmpty()) {
                System.out.println("  No books found matching: \"" + keyword + "\"");
                return;
            }
            System.out.println("  Found " + results.size() + " result(s):");
            printBookTableHeader();
            results.forEach(b -> System.out.println(b));
            ValidationUtil.printSeparator();
        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
        }
    }

    // ─── Admin: View All Users ─────────────────────────────────────────────────

    private static void adminViewAllUsers() {
        System.out.println("\n--- ALL REGISTERED USERS ---");
        try {
            List<User> users = userService.getAllUsers();
            if (users.isEmpty()) {
                System.out.println("  No users found.");
                return;
            }
            System.out.println("  " + String.format("| %-4s | %-15s | %-25s | %-12s | %-6s |",
                "ID", "Username", "Email", "Phone", "Role"));
            ValidationUtil.printSeparator();
            users.forEach(u -> System.out.println("  " + u));
            ValidationUtil.printSeparator();
            System.out.println("  Total users: " + users.size());
        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
        }
    }

    // ─── Admin: Delete User ────────────────────────────────────────────────────

    private static void adminDeleteUser(User admin) {
        System.out.println("\n--- DELETE USER ---");
        String idStr = readString("  Enter User ID to delete: ");

        if (!ValidationUtil.isPositiveNumber(idStr)) {
            System.out.println("[ERROR] Invalid user ID."); return;
        }
        int userId = Integer.parseInt(idStr.trim());

        System.out.print("  Are you sure? (yes/no): ");
        String confirm = sc.nextLine().trim();
        if (!"yes".equalsIgnoreCase(confirm)) {
            System.out.println("  Deletion cancelled."); return;
        }

        try {
            boolean success = userService.deleteUser(userId);
            if (success) {
                System.out.println("\n  [SUCCESS] User ID " + userId + " deleted.");
                FileUtil.logAdminAction(admin.getUsername(), "DELETE_USER", "UID:" + userId);
            }
        } catch (InvalidUserException e) {
            System.out.println("[ERROR] " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
        }
    }

    // ─── Admin: View All Transactions ─────────────────────────────────────────

    private static void adminViewAllTransactions() {
        System.out.println("\n--- ALL TRANSACTIONS ---");
        try {
            List<String> transactions = libraryService.getAllTransactions();
            if (transactions.isEmpty()) {
                System.out.println("  No transactions found.");
                return;
            }
            System.out.printf("  | %-4s | %-15s | %-30s | %-12s | %-12s | %-12s | %-10s | %-9s |%n",
                "TID", "Username", "Book Name", "Borrow", "Due", "Return", "Fine", "Status");
            ValidationUtil.printSeparator();
            transactions.forEach(t -> System.out.println("  " + t));
            ValidationUtil.printSeparator();
        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  USER MENU
    // ═══════════════════════════════════════════════════════════════════════════

    private static void showUserMenu(User user) {
        boolean loggedIn = true;

        while (loggedIn) {
            printUserMenu();
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1  -> userSearchBooks();
                case 2  -> userBorrowBook(user);
                case 3  -> userReturnBook(user);
                case 4  -> userViewHistory(user);
                case 5  -> {
                    System.out.println("\n  Logged out. See you next time, " + user.getUsername() + "!");
                    loggedIn = false;
                }
                default -> System.out.println("[!] Invalid choice. Please enter 1-5.");
            }
        }
    }

    private static void printUserMenu() {
        System.out.println();
        ValidationUtil.printSeparator();
        System.out.println("  USER MENU");
        ValidationUtil.printSeparator();
        System.out.println("  1. Search Books");
        System.out.println("  2. Borrow a Book");
        System.out.println("  3. Return a Book");
        System.out.println("  4. View My Borrow History");
        System.out.println("  5. Logout");
        ValidationUtil.printSeparator();
    }

    // ─── User: Search Books ────────────────────────────────────────────────────

    private static void userSearchBooks() {
        System.out.println("\n--- SEARCH BOOKS ---");
        String keyword = readString("  Enter keyword (title / author / category): ");

        try {
            List<Book> results = libraryService.searchBooks(keyword);
            if (results.isEmpty()) {
                System.out.println("  No books found matching: \"" + keyword + "\"");
                return;
            }
            System.out.println("  Found " + results.size() + " result(s):");
            printBookTableHeader();
            results.forEach(b -> System.out.println(b));
            ValidationUtil.printSeparator();
        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
        }
    }

    // ─── User: Borrow Book ─────────────────────────────────────────────────────

    private static void userBorrowBook(User user) {
        System.out.println("\n--- BORROW A BOOK ---");
        System.out.println("  (Tip: Use Search to find the Book ID first)");
        String bookId = readString("  Enter Book ID to borrow: ").toUpperCase().trim();

        try {
            libraryService.borrowBook(bookId, user.getUserId());
            FileUtil.logTransaction("BORROW", user.getUsername(), bookId);
        } catch (BookNotFoundException e) {
            System.out.println("[ERROR] " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
        }
    }

    // ─── User: Return Book ─────────────────────────────────────────────────────

    private static void userReturnBook(User user) {
        System.out.println("\n--- RETURN A BOOK ---");
        String bookId = readString("  Enter Book ID to return: ").toUpperCase().trim();

        try {
            double fine = libraryService.returnBook(bookId, user.getUserId());

            System.out.println("\n  [SUCCESS] Book returned successfully.");

            if (fine > 0) {
                System.out.printf("  [FINE] You have an overdue fine of Rs. %.2f. Please pay at the counter.%n", fine);
            } else {
                System.out.println("  No fine. Thank you for returning on time!");
            }

            FileUtil.logTransaction("RETURN", user.getUsername(), bookId);

        } catch (BookNotFoundException e) {
            System.out.println("[ERROR] " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
        }
    }

    // ─── User: View History ────────────────────────────────────────────────────

    private static void userViewHistory(User user) {
        System.out.println("\n--- MY BORROW HISTORY ---");

        try {
            List<String> history = libraryService.getBorrowHistory(user.getUserId());
            if (history.isEmpty()) {
                System.out.println("  You have not borrowed any books yet.");
                return;
            }
            System.out.printf("  | %-4s | %-35s | %-12s | %-12s | %-12s | %-10s | %-9s |%n",
                "TID", "Book Name", "Borrow", "Due", "Return", "Fine", "Status");
            ValidationUtil.printSeparator();
            history.forEach(h -> System.out.println("  " + h));
            ValidationUtil.printSeparator();

        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  SHARED DISPLAY HELPERS
    // ═══════════════════════════════════════════════════════════════════════════

    /** Prints the column header row for book tables. */
    private static void printBookTableHeader() {
        ValidationUtil.printSeparator();
        System.out.printf("| %-6s | %-35s | %-20s | %-12s | %-4s | %-15s |%n",
            "ID", "Title", "Author", "Category", "Qty", "ISBN");
        ValidationUtil.printSeparator();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  INPUT HELPERS
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Reads a non-empty string from the user.
     *
     * @param  prompt  message shown to the user
     * @return trimmed non-empty string
     */
    private static String readString(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = sc.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("  [!] Input cannot be empty. Please try again.");
            }
        } while (input.isEmpty());
        return input;
    }

    /**
     * Reads a string that may optionally be blank (used for update fields).
     *
     * @param  prompt  message shown to the user
     * @return trimmed string, possibly empty
     */
    private static String readStringOptional(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    /**
     * Reads an integer from the user, retrying on invalid input.
     *
     * @param  prompt  message shown to the user
     * @return valid integer value
     */
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Please enter a valid number.");
            }
        }
    }
}
