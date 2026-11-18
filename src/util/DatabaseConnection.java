package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection — Singleton pattern for MySQL connection.
 * 
 * IMPORTANT: Update the URL, USER, and PASSWORD constants
 * to match your local MySQL configuration before running.
 */
public class DatabaseConnection {

    // ── MySQL connection parameters ──────────────────────────
    private static final String URL = envOrDefault(
            "UNIROOM_DB_URL",
            "jdbc:mysql://localhost:3306/uniroom?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
    private static final String USER = envOrDefault("UNIROOM_DB_USER", "root");
    private static final String PASS = envOrDefault("UNIROOM_DB_PASSWORD", "");

    private static Connection connection;

    private static String envOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    // Private constructor prevents external instantiation
    private DatabaseConnection() {
    }

    /**
     * Returns a shared connection to the MySQL database.
     * Creates one if it doesn't exist or is closed.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load the MySQL JDBC driver (modern)
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("✓ Database connected successfully.");
            } catch (ClassNotFoundException e) {
                // Fallback or throw error
                throw new SQLException("MySQL JDBC Driver not found. Ensure mysql-connector-j is in the classpath.", e);
            }
        }
        return connection;
    }

    /**
     * Closes the database connection gracefully.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
