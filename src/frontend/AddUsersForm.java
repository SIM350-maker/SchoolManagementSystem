package frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AddUsersForm extends JFrame {
    private JTextField usernameField, roleField;
    private JPasswordField passwordField;

    public AddUsersForm() {
        setTitle("Add User");
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

        JButton addButton = new JButton("Add User");
        panel.add(addButton);
        addButton.addActionListener(new AddUserActionListener());

        setVisible(true);
    }

    private class AddUserActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = roleField.getText();

            if (username.isEmpty() || password.isEmpty() || role.isEmpty()) {
                JOptionPane.showMessageDialog(AddUsersForm.this, "All fields must be filled out.");
                return;
            }

            // Now we don't need to store the password_hash field
            String query = "INSERT INTO users (username, password, role, created_at) VALUES (?, ?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, username);           // Set username
                stmt.setString(2, password);           // Set password (plain text)
                stmt.setString(3, role);               // Set role
                stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis())); // Set created_at timestamp
                stmt.executeUpdate();                  // Execute the insert query

                JOptionPane.showMessageDialog(AddUsersForm.this, "User added successfully.");
                dispose(); // Close the form after adding the user
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(AddUsersForm.this, "Error adding user: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AddUsersForm::new);
    }
}
