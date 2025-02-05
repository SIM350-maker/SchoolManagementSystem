package frontend;

import backend.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateEnrollmentForm {
    private JFrame frame;
    private JTextField studentIdField, classIdField;
    private JButton updateButton;
    private int enrollmentId;

    public UpdateEnrollmentForm(int enrollmentId) {
        this.enrollmentId = enrollmentId;

        frame = new JFrame("Update Enrollment");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(4, 2, 10, 10));

        frame.add(new JLabel("Student ID:"));
        studentIdField = new JTextField();
        frame.add(studentIdField);

        frame.add(new JLabel("Class ID:"));
        classIdField = new JTextField();
        frame.add(classIdField);

        updateButton = new JButton("Update Enrollment");
        frame.add(updateButton);

        updateButton.addActionListener(_ -> updateEnrollment());

        loadEnrollmentDetails();
        frame.setVisible(true);
    }

    private void loadEnrollmentDetails() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT student_id, class_id FROM enrollments WHERE enrollment_id = ?")) {

            pstmt.setInt(1, enrollmentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                studentIdField.setText(String.valueOf(rs.getInt("student_id")));
                classIdField.setText(String.valueOf(rs.getInt("class_id")));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error loading enrollment details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEnrollment() {
        int studentId = Integer.parseInt(studentIdField.getText().trim());
        int classId = Integer.parseInt(classIdField.getText().trim());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE enrollments SET student_id = ?, class_id = ? WHERE enrollment_id = ?")) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, classId);
            pstmt.setInt(3, enrollmentId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(frame, "Enrollment updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error updating enrollment: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
