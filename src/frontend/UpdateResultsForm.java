package frontend;

import backend.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateResultsForm {
    private JFrame frame;
    private JTextField marksField, gradeField;
    private JButton updateButton, deleteButton;
    private int resultId;

    
    public UpdateResultsForm(int resultId) {
        this.resultId = resultId;

        frame = new JFrame("Update Exam Result");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(3, 2, 10, 10));

        frame.add(new JLabel("Marks Obtained:"));
        marksField = new JTextField();
        frame.add(marksField);

        frame.add(new JLabel("Grade:"));
        gradeField = new JTextField();
        frame.add(gradeField);

        updateButton = new JButton("Update Result");
        deleteButton = new JButton("Delete Result");

        frame.add(updateButton);
        frame.add(deleteButton);

        loadResultDetails();

        updateButton.addActionListener(_ -> updateResult());
        deleteButton.addActionListener(_ -> deleteResult());

        frame.setVisible(true);
    }

    private void loadResultDetails() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT marks_obtained, grade FROM results WHERE result_id = ?")) {
            pstmt.setInt(1, resultId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                marksField.setText(String.valueOf(rs.getInt("marks_obtained")));
                gradeField.setText(rs.getString("grade"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error loading result: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateResult() {
        int marks = Integer.parseInt(marksField.getText().trim());
        String grade = gradeField.getText().trim();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE results SET marks_obtained = ?, grade = ? WHERE result_id = ?")) {
            pstmt.setInt(1, marks);
            pstmt.setString(2, grade);
            pstmt.setInt(3, resultId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(frame, "Result updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error updating result: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteResult() {
        int confirmation = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this result?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM results WHERE result_id = ?")) {
                pstmt.setInt(1, resultId);
                int rowsDeleted = pstmt.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(frame, "Result deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(frame, "Error deleting result: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
