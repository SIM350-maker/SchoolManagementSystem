package frontend;

import backend.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddTeacherForm {
    private JFrame frame;
    private JTextField firstNameField, lastNameField, subjectField, emailField, phoneField, hireDateField;
    private JButton saveButton, cancelButton;

    
    public AddTeacherForm() {
        // Initialize the frame
        frame = new JFrame("Add Teacher");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(7, 2, 5, 5));

        // Create and add components
        frame.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        frame.add(firstNameField);

        frame.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        frame.add(lastNameField);

        frame.add(new JLabel("Subject:"));
        subjectField = new JTextField();
        frame.add(subjectField);

        frame.add(new JLabel("Email:"));
        emailField = new JTextField();
        frame.add(emailField);

        frame.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        frame.add(phoneField);

        frame.add(new JLabel("Hire Date (YYYY-MM-DD):"));
        hireDateField = new JTextField();
        frame.add(hireDateField);

        // Buttons for save and cancel
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        frame.add(saveButton);
        frame.add(cancelButton);

        // Add action listeners for the buttons
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTeacher();  // Save teacher details to database
            }
        });

        cancelButton.addActionListener(_ -> frame.dispose());  // Close the form

        // Display the frame
        frame.setVisible(true);
    }

    private void addTeacher() {
        // Get data from the form fields
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String subject = subjectField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String hireDate = hireDateField.getText().trim();

        // Validation checks for fields
        if (firstName.isEmpty() || lastName.isEmpty() || subject.isEmpty() || email.isEmpty() || phone.isEmpty() || hireDate.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "All fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Try to establish a connection and insert the data
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO teachers (first_name, last_name, subject, email, phone, hire_date) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, firstName);
                pstmt.setString(2, lastName);
                pstmt.setString(3, subject);
                pstmt.setString(4, email);
                pstmt.setString(5, phone);
                pstmt.setString(6, hireDate);

                // Execute the update
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(frame, "Teacher added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearFormFields();  // Clear the form after saving
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to add teacher.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFormFields() {
        // Clear all the form fields
        firstNameField.setText("");
        lastNameField.setText("");
        subjectField.setText("");
        emailField.setText("");
        phoneField.setText("");
        hireDateField.setText("");
    }
}
