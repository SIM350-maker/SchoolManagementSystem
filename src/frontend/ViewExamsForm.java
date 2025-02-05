
package frontend;

import backend.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class ViewExamsForm {
    private JFrame frame;
    private JTable examsTable;
    private JButton updateButton;
    private JButton deleteButton;

    
    public ViewExamsForm() {
        frame = new JFrame("View Exams");
        frame.setSize(800, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        examsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(examsTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        updateButton = new JButton("Update Exam");
        deleteButton = new JButton("Delete Exam");

        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        updateButton.addActionListener(_ -> updateExam());
        deleteButton.addActionListener(_ -> deleteExam());

        loadExams();
        frame.setVisible(true);
    }

    private void loadExams() {
        Vector<Vector<Object>> data = new Vector<>();
        Vector<String> columnNames = new Vector<>();

        columnNames.add("Exam ID");
        columnNames.add("Exam Name");
        columnNames.add("Subject ID");
        columnNames.add("Exam Date");
        columnNames.add("Total Marks");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT exam_id, exam_name, subject_id, exam_date, total_marks FROM exams");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("exam_id"));
                row.add(rs.getString("exam_name"));
                row.add(rs.getInt("subject_id"));
                row.add(rs.getDate("exam_date"));
                row.add(rs.getInt("total_marks"));
                data.add(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error loading exams: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        examsTable.setModel(model);
    }

    private void updateExam() {
        int selectedRow = examsTable.getSelectedRow();
        if (selectedRow != -1) {
            int examId = (int) examsTable.getValueAt(selectedRow, 0);
            new UpdateExamsForm(examId);
        } else {
            JOptionPane.showMessageDialog(frame, "Please select an exam to update", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteExam() {
        int selectedRow = examsTable.getSelectedRow();
        if (selectedRow != -1) {
            int examId = (int) examsTable.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this exam?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement("DELETE FROM exams WHERE exam_id = ?")) {

                    pstmt.setInt(1, examId);
                    int affectedRows = pstmt.executeUpdate();

                    if (affectedRows > 0) {
                        JOptionPane.showMessageDialog(frame, "Exam deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadExams(); // Reload data after deletion
                    } else {
                        JOptionPane.showMessageDialog(frame, "Failed to delete exam", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(frame, "Error deleting exam: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please select an exam to delete", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
}
