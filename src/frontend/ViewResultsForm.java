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

public class ViewResultsForm {
    private JFrame frame;
    private JTable resultsTable;
    private JButton updateButton, deleteButton;

    
    public ViewResultsForm() {
        frame = new JFrame("Manage Results");
        frame.setSize(800, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        resultsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        updateButton = new JButton("Update Result");
        deleteButton = new JButton("Delete Result");

        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        updateButton.addActionListener(_ -> updateResult());
        deleteButton.addActionListener(_ -> deleteResult());

        loadResults();
        frame.setVisible(true);
    }

    private void loadResults() {
        Vector<Vector<Object>> data = new Vector<>();
        Vector<String> columnNames = new Vector<>();

        columnNames.add("Result ID");
        columnNames.add("Student ID");
        columnNames.add("Exam ID");
        columnNames.add("Marks Obtained");
        columnNames.add("Grade");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT result_id, student_id, exam_id, marks_obtained, grade FROM results");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("result_id"));
                row.add(rs.getInt("student_id"));
                row.add(rs.getInt("exam_id"));
                row.add(rs.getInt("marks_obtained"));
                row.add(rs.getString("grade"));
                data.add(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error loading results: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        resultsTable.setModel(model);
    }

    private void updateResult() {
        int selectedRow = resultsTable.getSelectedRow();
        if (selectedRow != -1) {
            int resultId = (int) resultsTable.getValueAt(selectedRow, 0);
            new UpdateResultsForm(resultId);
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a result to update", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteResult() {
        int selectedRow = resultsTable.getSelectedRow();
        if (selectedRow != -1) {
            int resultId = (int) resultsTable.getValueAt(selectedRow, 0);
            
            int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this result?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement("DELETE FROM results WHERE result_id = ?")) {

                    pstmt.setInt(1, resultId);
                    int rowsDeleted = pstmt.executeUpdate();
                    
                    if (rowsDeleted > 0) {
                        JOptionPane.showMessageDialog(frame, "Result deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadResults(); // Refresh table
                    } else {
                        JOptionPane.showMessageDialog(frame, "Failed to delete result!", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(frame, "Error deleting result: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a result to delete", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
}
