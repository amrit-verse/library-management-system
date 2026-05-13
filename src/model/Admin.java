package model;

/**
 * Admin - Represents a library administrator.
 *
 * OOP Concepts:
 *   - Inheritance   : extends User and reuses all User fields/methods
 *   - Polymorphism  : overrides getRole() to always return "ADMIN"
 *
 * An Admin has all User capabilities PLUS administrative privileges
 * such as adding/updating/deleting books and managing users.
 */
public class Admin extends User {

    /** Admin's designation or department (optional extra field). */
    private String designation;

    // ─── Constructors ──────────────────────────────────────────────────────────

    /** Default constructor. */
    public Admin() {
        super();
    }

    /**
     * Constructor used when loading an admin from the database.
     *
     * @param userId      primary key from DB
     * @param username    admin login name
     * @param password    admin password
     * @param email       admin email
     * @param phone       admin phone
     */
    public Admin(int userId, String username, String password,
                 String email, String phone) {
        super(userId, username, password, email, phone, "ADMIN");
        this.designation = "Library Administrator";
    }

    /**
     * Constructor with custom designation.
     */
    public Admin(int userId, String username, String password,
                 String email, String phone, String designation) {
        super(userId, username, password, email, phone, "ADMIN");
        this.designation = designation;
    }

    // ─── Overridden Methods ────────────────────────────────────────────────────

    /**
     * Polymorphism: Admin always returns "ADMIN" regardless of any setRole call.
     */
    @Override
    public String getRole() {
        return "ADMIN";
    }

    /**
     * Admin is always considered an admin.
     */
    @Override
    public boolean isAdmin() {
        return true;
    }

    // ─── Getter / Setter ───────────────────────────────────────────────────────

    public String getDesignation()             { return designation;   }
    public void setDesignation(String desig)   { this.designation = desig; }

    @Override
    public String toString() {
        return String.format(
            "| %-4d | %-15s | %-25s | %-12s | %-22s |",
            getUserId(), getUsername(), getEmail(), getPhone(), designation
        );
    }
}
