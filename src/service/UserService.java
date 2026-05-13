package service;

import database.DBConnection;
import exception.InvalidUserException;
import model.Admin;
import model.User;
import util.FileUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserService - Handles all user-related operations.
 *
 * OOP Concepts:
 *   - Abstraction    : hides SQL details from the caller
 *   - Encapsulation  : private helper methods for DB operations
 *   - Polymorphism   : returns User or Admin based on DB role
 *
 * Responsibilities:
 *   - Register a new user
 *   - Login (returns User or Admin object)
 *   - View all users (admin only)
 *   - Delete a user (admin only)
 */
public class UserService {

    // ─── Registration ──────────────────────────────────────────────────────────

    /**
     * Registers a new user in the database.
     * Validates that username and email are not already taken.
     *
     * @param  user  the User object to register
     * @return true if registration was successful
     * @throws InvalidUserException if username or email already exists
     * @throws SQLException         on database error
     */
    public boolean registerUser(User user) throws InvalidUserException, SQLException {

        // Check for duplicate username
        if (usernameExists(user.getUsername())) {
            throw new InvalidUserException(user.getUsername(),
                "Username '" + user.getUsername() + "' is already taken.");
        }

        // Check for duplicate email
        if (emailExists(user.getEmail())) {
            throw new InvalidUserException(user.getUsername(),
                "Email '" + user.getEmail() + "' is already registered.");
        }

        String sql = "INSERT INTO users (username, password, email, phone, role) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getRole());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                FileUtil.logInfo("New user registered: " + user.getUsername());
                return true;
            }
        }
        return false;
    }

    // ─── Login ─────────────────────────────────────────────────────────────────

    /**
     * Authenticates a user by username and password.
     * Returns an Admin object if the user has the ADMIN role,
     * otherwise returns a plain User object.
     *
     * @param  username  login name
     * @param  password  plain-text password
     * @return User or Admin object on success
     * @throws InvalidUserException if credentials are incorrect
     * @throws SQLException         on database error
     */
    public User login(String username, String password)
            throws InvalidUserException, SQLException {

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    FileUtil.logInfo("Login success: " + username + " [" + role + "]");

                    // Polymorphism: return Admin or User based on role
                    if ("ADMIN".equals(role)) {
                        return new Admin(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("phone")
                        );
                    } else {
                        return new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            role
                        );
                    }
                } else {
                    throw new InvalidUserException(username,
                        "Invalid username or password. Please try again.");
                }
            }
        }
    }

    // ─── View All Users ────────────────────────────────────────────────────────

    /**
     * Retrieves all registered users from the database.
     * Intended for admin use only.
     *
     * @return List of User objects
     * @throws SQLException on database error
     */
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY user_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("role")
                );
                users.add(user);
            }
        }
        return users;
    }

    // ─── Delete User ───────────────────────────────────────────────────────────

    /**
     * Deletes a user by user ID.
     * Prevents deletion of the default admin account (user_id = 1).
     *
     * @param  userId  the ID of the user to delete
     * @return true if the user was successfully deleted
     * @throws InvalidUserException if the user is not found or deletion is blocked
     * @throws SQLException         on database error
     */
    public boolean deleteUser(int userId) throws InvalidUserException, SQLException {

        if (userId == 1) {
            throw new InvalidUserException("Cannot delete the default admin account.");
        }

        if (!userIdExists(userId)) {
            throw new InvalidUserException("No user found with ID: " + userId);
        }

        // Check if user has active borrows before deleting
        if (hasActiveBorrows(userId)) {
            throw new InvalidUserException(
                "User has active borrowed books. Cannot delete until all books are returned.");
        }

        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                FileUtil.logInfo("User deleted. ID: " + userId);
                return true;
            }
        }
        return false;
    }

    // ─── Private Helpers ───────────────────────────────────────────────────────

    /**
     * Checks if a username already exists in the database.
     *
     * @param  username  the username to check
     * @return true if exists
     */
    private boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Checks if an email already exists in the database.
     *
     * @param  email  the email to check
     * @return true if exists
     */
    private boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Checks if a user ID exists in the database.
     *
     * @param  userId  the user ID to check
     * @return true if exists
     */
    private boolean userIdExists(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Checks if a user currently has any unreturned borrowed books.
     *
     * @param  userId  the user ID to check
     * @return true if there are active borrows
     */
    private boolean hasActiveBorrows(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM transactions WHERE user_id = ? AND status = 'BORROWED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}
