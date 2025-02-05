package frontend;

import backend.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class UpdateStudentForm {
    private JFrame frame;
    private JTextField studentIdField, firstNameField, lastNameField, dobField, emailField, phoneField, classIdField, genderField;
    private JButton updateButton, cancelButton;
    private int studentId;

    public UpdateStudentForm(int studentId) {
        this.studentId = studentId;

        frame = new JFrame("Update Student");
        frame.setSize(400, 400); // Adjusted size since Search button is removed
        frame.setLayout(new GridLayout(9, 2, 5, 5)); // Adjusted grid layout
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Search field (already populated with student ID)
        frame.add(new JLabel("Student ID:"));
        studentIdField = new JTextField(String.valueOf(studentId));
        studentIdField.setEditable(false);  // Make it uneditable
        frame.add(studentIdField);

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
        updateButton = new JButton("Update");
        cancelButton = new JButton("Cancel");

        frame.add(updateButton);
        frame.add(cancelButton);

        // Disable fields until student is searched
        setFieldsEditable(true); // Removed dependency on Search button

        // Button actions
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStudent();
            }
        });

        cancelButton.addActionListener(_ -> frame.dispose());

        // Automatically populate the fields with the student's data
        populateFields();

        frame.setVisible(true);
    }

    private void populateFields() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM students WHERE student_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                firstNameField.setText(rs.getString("first_name"));
                lastNameField.setText(rs.getString("last_name"));
                dobField.setText(rs.getString("date_of_birth"));
                genderField.setText(rs.getString("gender"));
                classIdField.setText(String.valueOf(rs.getInt("class_id")));
                emailField.setText(rs.getString("email"));
                phoneField.setText(rs.getString("phone"));

                setFieldsEditable(true);
            } else {
                JOptionPane.showMessageDialog(frame, "No student found with ID: " + studentId, "Not Found", JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void updateStudent() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String dateOfBirth = dobField.getText().trim();
        String gender = genderField.getText().trim();
        String classId = classIdField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || dateOfBirth.isEmpty() || gender.isEmpty() ||
                classId.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "All fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE students SET first_name = ?, last_name = ?, date_of_birth = ?, gender = ?, class_id = ?, email = ?, phone = ? WHERE student_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, dateOfBirth);
            pstmt.setString(4, gender);
            pstmt.setInt(5, Integer.parseInt(classId));
            pstmt.setString(6, email);
            pstmt.setString(7, phone);
            pstmt.setInt(8, studentId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(frame, "Student updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to update student.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid input for Class ID or Student ID!", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void setFieldsEditable(boolean editable) {
        firstNameField.setEditable(editable);
        lastNameField.setEditable(editable);
        dobField.setEditable(editable);
        genderField.setEditable(editable);
        classIdField.setEditable(editable);
        emailField.setEditable(editable);
        phoneField.setEditable(editable);
        updateButton.setEnabled(editable);
    }
}
