package frontend;

import backend.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddResultsForm {
    private JFrame frame;
    private JTextField studentIdField, examIdField, marksField, gradeField;
    private JButton addButton;

    
    public AddResultsForm() {
        frame = new JFrame("Add Exam Results");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(5, 2, 10, 10));

        frame.add(new JLabel("Student ID:"));
        studentIdField = new JTextField();
        frame.add(studentIdField);

        frame.add(new JLabel("Exam ID:"));
        examIdField = new JTextField();
        frame.add(examIdField);

        frame.add(new JLabel("Marks Obtained:"));
        marksField = new JTextField();
        frame.add(marksField);

        frame.add(new JLabel("Grade:"));
        gradeField = new JTextField();
        frame.add(gradeField);

        addButton = new JButton("Add Result");
        frame.add(addButton);

        addButton.addActionListener(_ -> addResult());

        frame.setVisible(true);
    }

    private void addResult() {
        int studentId = Integer.parseInt(studentIdField.getText().trim());
        int examId = Integer.parseInt(examIdField.getText().trim());
        int marks = Integer.parseInt(marksField.getText().trim());
        String grade = gradeField.getText().trim();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO results (student_id, exam_id, marks_obtained, grade) VALUES (?, ?, ?, ?)")) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, examId);
            pstmt.setInt(3, marks);
            pstmt.setString(4, grade);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(frame, "Result added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error adding result: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
