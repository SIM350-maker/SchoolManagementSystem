package frontend;

import backend.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class AddEnrollmentForm {
    private JFrame frame;
    private JTextField studentIdField, classIdField;
    private JButton submitButton;

    
    public AddEnrollmentForm() {
        frame = new JFrame("Add Enrollment");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(4, 2, 10, 10));

        frame.add(new JLabel("Student ID:"));
        studentIdField = new JTextField();
        frame.add(studentIdField);

        frame.add(new JLabel("Class ID:"));
        classIdField = new JTextField();
        frame.add(classIdField);

        submitButton = new JButton("Add Enrollment");
        frame.add(submitButton);

        submitButton.addActionListener(_ -> addEnrollment());

        frame.setVisible(true);
    }

    private void addEnrollment() {
        int studentId = Integer.parseInt(studentIdField.getText().trim());
        int classId = Integer.parseInt(classIdField.getText().trim());
        LocalDate enrollmentDate = LocalDate.now(); // Set current date

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO enrollments (student_id, class_id, enrollment_date) VALUES (?, ?, ?)")) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, classId);
            pstmt.setDate(3, java.sql.Date.valueOf(enrollmentDate));

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(frame, "Enrollment added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to add enrollment!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error adding enrollment: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
