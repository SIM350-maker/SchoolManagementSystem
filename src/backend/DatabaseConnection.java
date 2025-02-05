
package backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    // MySQL database credentials
    private static final String URL = "jdbc:mysql://localhost:3306/highschool_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";  // Replace with your actual MySQL password
    
    // Establish connection to the database
    public static Connection getConnection() {
        try {
            // Register MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Create and return the connection
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error in database connection: " + e.getMessage());
            return null;
        }
    }
}
