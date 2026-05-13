package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection - Singleton class for managing MySQL JDBC Connection.
 *
 * Design Pattern : Singleton
 * OOP Concept    : Encapsulation (private constructor + static accessor)
 *
 * Only one Connection object is created and reused throughout the app.
 * Before using: place mysql-connector-j-*.jar in /lib and add to classpath.
 */
public class DBConnection {

    // ---------------------------------------------------------------
    // !! CHANGE THESE VALUES to match your MySQL setup !!
    // ---------------------------------------------------------------
    private static final String DB_URL = "jdbc:mariadb://localhost:3306/library_db";
    private static final String DB_USER     = "root";
    private static final String DB_PASSWORD = "pass";   // ← change this
    // ---------------------------------------------------------------

    /** Holds the single connection instance. */
    private static Connection connection = null;

    /** Private constructor prevents external instantiation. */
    private DBConnection() {}

    /**
     * Returns the active Connection.
     * Creates a new one if none exists or the previous one was closed.
     *
     * @return  java.sql.Connection object
     * @throws  SQLException if the driver is missing or credentials are wrong
     */
    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                // Load the MySQL JDBC driver class
                Class.forName("org.mariadb.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("[DB] Connected to MariaDB successfully.");
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "[DB ERROR] MariaDB JDBC Driver not found."+
                "  Fix: Add mysql-connector-j-*.jar to your classpath / lib folder.", e);
        }
        return connection;
    }

    /**
     * Closes the database connection safely.
     * Call this when the application exits.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed cleanly.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error while closing connection: " + e.getMessage());
        }
    }
}
