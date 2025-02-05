
package frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class UpdateUsersForm extends JFrame {
    private JTextField usernameField, roleField;
    private JPasswordField passwordField;
    private int userId;

    public UpdateUsersForm(int userId) {
        this.userId = userId;
        setTitle("Update User");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the form panel
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        add(panel);

        // Create and add components to the panel
        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        panel.add(new JLabel("Role:"));
        roleField = new JTextField();
        panel.add(roleField);

        JButton updateButton = new JButton("Update User");
        panel.add(updateButton);
        updateButton.addActionListener(new UpdateUserActionListener());

        // Load user data to pre-fill the form fields
        loadUserData();

        setVisible(true);
    }

    private void loadUserData() {
        // Modify the query to fetch data without password_hash
        String query = "SELECT username, password, role FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                usernameField.setText(rs.getString("username"));
                passwordField.setText(rs.getString("password")); // Set the password field
                roleField.setText(rs.getString("role"));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading user data: " + e.getMessage());
        }
    }

    private class UpdateUserActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = roleField.getText();

            if (username.isEmpty() || password.isEmpty() || role.isEmpty()) {
                JOptionPane.showMessageDialog(UpdateUsersForm.this, "All fields must be filled out.");
                return;
            }

            // Using the password directly (no hashing)
            String query = "UPDATE users SET username = ?, password = ?, role = ? WHERE user_id = ?"; // Use password column directly

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, username);
                stmt.setString(2, password); // Store password directly in plain text
                stmt.setString(3, role);
                stmt.setInt(4, userId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(UpdateUsersForm.this, "User updated successfully.");
                dispose(); // Close the form after updating the user
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(UpdateUsersForm.this, "Error updating user: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UpdateUsersForm(1)); // Example userId for testing
    }
}
