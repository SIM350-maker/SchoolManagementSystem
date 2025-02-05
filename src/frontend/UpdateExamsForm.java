package frontend;

import backend.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateExamsForm {
    private JFrame frame;
    private JTextField examNameField, subjectIdField, examDateField, totalMarksField;
    private JButton updateButton;
    private int examId;

    
    public UpdateExamsForm(int examId) {
        this.examId = examId;
        frame = new JFrame("Update Exam");
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(5, 2, 10, 10));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.add(new JLabel("Exam Name:"));
        examNameField = new JTextField();
        frame.add(examNameField);

        frame.add(new JLabel("Subject ID:"));
        subjectIdField = new JTextField();
        frame.add(subjectIdField);

        frame.add(new JLabel("Exam Date (YYYY-MM-DD):"));
        examDateField = new JTextField();
        frame.add(examDateField);

        frame.add(new JLabel("Total Marks:"));
        totalMarksField = new JTextField();
        frame.add(totalMarksField);

        updateButton = new JButton("Update Exam");
        frame.add(updateButton);

        updateButton.addActionListener(_ -> updateExam());

        loadExamDetails();
        frame.setVisible(true);
    }

    private void loadExamDetails() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM exams WHERE exam_id = ?")) {
            pstmt.setInt(1, examId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                examNameField.setText(rs.getString("exam_name"));
                subjectIdField.setText(String.valueOf(rs.getInt("subject_id")));
                examDateField.setText(rs.getString("exam_date"));
                totalMarksField.setText(String.valueOf(rs.getInt("total_marks")));
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error loading exam details: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateExam() {
        String examName = examNameField.getText();
        String subjectId = subjectIdField.getText();
        String examDate = examDateField.getText();
        String totalMarks = totalMarksField.getText();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE exams SET exam_name=?, subject_id=?, exam_date=?, total_marks=? WHERE exam_id=?")) {
            pstmt.setString(1, examName);
            pstmt.setInt(2, Integer.parseInt(subjectId));
            pstmt.setString(3, examDate);
            pstmt.setInt(4, Integer.parseInt(totalMarks));
            pstmt.setInt(5, examId);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Exam updated successfully!");
            frame.dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error updating exam: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
