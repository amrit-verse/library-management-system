package model;

/**
 * User - Model class representing a library member.
 *
 * OOP Concepts:
 *   - Encapsulation  : private fields with public getters/setters
 *   - Inheritance    : Admin.java extends this class
 *   - Polymorphism   : getRole() is overridden in Admin
 */
public class User {

    // ─── Fields ────────────────────────────────────────────────────────────────
    private int    userId;      // Auto-generated primary key from DB
    private String username;    // Login name (unique)
    private String password;    // Plain-text password (hash in production)
    private String email;       // User's email address (unique)
    private String phone;       // Contact number
    private String role;        // 'USER' or 'ADMIN'

    // ─── Constructors ──────────────────────────────────────────────────────────

    /** Default constructor. */
    public User() {}

    /**
     * Constructor without userId — used for new registrations.
     *
     * @param username login name
     * @param password user's password
     * @param email    email address
     * @param phone    phone number
     */
    public User(String username, String password, String email, String phone) {
        this.username = username;
        this.password = password;
        this.email    = email;
        this.phone    = phone;
        this.role     = "USER"; // default role
    }

    /**
     * Full constructor — used when loading a user from the database.
     */
    public User(int userId, String username, String password,
                String email, String phone, String role) {
        this.userId   = userId;
        this.username = username;
        this.password = password;
        this.email    = email;
        this.phone    = phone;
        this.role     = role;
    }

    // ─── Getters ───────────────────────────────────────────────────────────────

    public int    getUserId()  { return userId;   }
    public String getUsername(){ return username; }
    public String getPassword(){ return password; }
    public String getEmail()   { return email;    }
    public String getPhone()   { return phone;    }

    /**
     * Returns the role of this user.
     * Overridden in Admin to always return "ADMIN".
     *
     * @return role string ("USER" or "ADMIN")
     */
    public String getRole() { return role; }

    // ─── Setters ───────────────────────────────────────────────────────────────

    public void setUserId(int userId)      { this.userId   = userId;   }
    public void setUsername(String u)      { this.username = u;        }
    public void setPassword(String p)      { this.password = p;        }
    public void setEmail(String email)     { this.email    = email;    }
    public void setPhone(String phone)     { this.phone    = phone;    }
    public void setRole(String role)       { this.role     = role;     }

    // ─── Utility ───────────────────────────────────────────────────────────────

    /** Returns true if this user has admin privileges. */
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.role);
    }

    @Override
    public String toString() {
        return String.format(
            "| %-4d | %-15s | %-25s | %-12s | %-6s |",
            userId, username, email, phone, role
        );
    }
}
