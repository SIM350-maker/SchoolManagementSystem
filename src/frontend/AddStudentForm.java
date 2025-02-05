package frontend;

import backend.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddStudentForm {

    private JFrame frame;
    private JTextField firstNameField, lastNameField, dobField, emailField, phoneField, classIdField, genderField;
    private JButton submitButton, cancelButton;

    
    public AddStudentForm() {
        frame = new JFrame("Add New Student");
        frame.setSize(400, 400);
        frame.setLayout(new GridLayout(9, 2, 5, 5)); // 9 rows, 2 columns
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create labels and input fields
        frame.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        frame.add(firstNameField);

        frame.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        frame.add(lastNameField);

        frame.add(new JLabel("Date of Birth (YYYY-MM-DD):"));
        dobField = new JTextField();
        frame.add(dobField);

        frame.add(new JLabel("Gender:"));
        genderField = new JTextField();
        frame.add(genderField);

        frame.add(new JLabel("Class ID:"));
        classIdField = new JTextField();
        frame.add(classIdField);

        frame.add(new JLabel("Email:"));
        emailField = new JTextField();
        frame.add(emailField);

        frame.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        frame.add(phoneField);

        // Buttons
        submitButton = new JButton("Submit");
        cancelButton = new JButton("Cancel");

        frame.add(submitButton);
        frame.add(cancelButton);

        // Submit button action
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStudent();
            }
        });

        // Cancel button action
        cancelButton.addActionListener(_ -> frame.dispose());

        frame.setVisible(true);
    }

    private void addStudent() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String dateOfBirth = dobField.getText().trim();
        String gender = genderField.getText().trim();
        String classId = classIdField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        // Input validation
        if (firstName.isEmpty() || lastName.isEmpty() || dateOfBirth.isEmpty() || gender.isEmpty() ||
                classId.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "All fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                JOptionPane.showMessageDialog(frame, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "INSERT INTO students (first_name, last_name, date_of_birth, gender, class_id, email, phone, registration_date) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, dateOfBirth);
            pstmt.setString(4, gender);
            pstmt.setInt(5, Integer.parseInt(classId));
            pstmt.setString(6, email);
            pstmt.setString(7, phone);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(frame, "Student added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to add student.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Class ID must be a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void clearFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        dobField.setText("");
        genderField.setText("");
        classIdField.setText("");
        emailField.setText("");
        phoneField.setText("");
    }
}
