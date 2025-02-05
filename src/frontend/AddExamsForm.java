package frontend;

import backend.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddExamsForm {
    private JFrame frame;
    private JTextField examNameField, subjectIdField, examDateField, totalMarksField;
    private JButton addButton;

    
    public AddExamsForm() {
        frame = new JFrame("Add Exam");
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

        addButton = new JButton("Add Exam");
        frame.add(addButton);

        addButton.addActionListener(_ -> addExam());

        frame.setVisible(true);
    }

    private void addExam() {
        String examName = examNameField.getText();
        String subjectId = subjectIdField.getText();
        String examDate = examDateField.getText();
        String totalMarks = totalMarksField.getText();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO exams (exam_name, subject_id, exam_date, total_marks) VALUES (?, ?, ?, ?)")) {
            pstmt.setString(1, examName);
            pstmt.setInt(2, Integer.parseInt(subjectId));
            pstmt.setString(3, examDate);
            pstmt.setInt(4, Integer.parseInt(totalMarks));

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Exam added successfully!");
            frame.dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error adding exam: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
