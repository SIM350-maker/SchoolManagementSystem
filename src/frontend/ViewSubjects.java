package frontend;

import backend.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class ViewSubjects {
    private JFrame frame;
    private JTable subjectTable;
    private JButton updateButton, deleteButton;
    private JPanel panel;

    
    public ViewSubjects() {
        // Initialize the frame
        frame = new JFrame("View Subjects");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Set up the table and panel
        panel = new JPanel(new BorderLayout());
        subjectTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(subjectTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons for update and delete
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(panel);

        // Load subject data from the database
        loadSubjects();

        // Button actions
        updateButton.addActionListener(_ -> openUpdateSubjectForm());
        deleteButton.addActionListener(_ -> deleteSubject());

        // Display the frame
        frame.setVisible(true);
    }

    private void loadSubjects() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT subject_id, subject_name FROM subjects");

            // Create table model
            Vector<Vector<Object>> data = new Vector<>();
            Vector<String> columns = new Vector<>();
            columns.add("Subject ID");
            columns.add("Subject Name");

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("subject_id"));
                row.add(rs.getString("subject_name"));
                data.add(row);
            }

            // Set table model
            subjectTable.setModel(new javax.swing.table.DefaultTableModel(data, columns));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error loading subjects: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void openUpdateSubjectForm() {
        int selectedRow = subjectTable.getSelectedRow();
        if (selectedRow != -1) {
            int subjectId = (int) subjectTable.getValueAt(selectedRow, 0);
            String subjectName = (String) subjectTable.getValueAt(selectedRow, 1);
            new UpdateSubjectForm(subjectId, subjectName);
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a subject to update.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSubject() {
        int selectedRow = subjectTable.getSelectedRow();
        if (selectedRow != -1) {
            int subjectId = (int) subjectTable.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this subject?", "Delete Subject", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String sql = "DELETE FROM subjects WHERE subject_id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setInt(1, subjectId);
                        int rowsAffected = pstmt.executeUpdate();
                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(frame, "Subject deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                            loadSubjects();  // Reload the table
                        } else {
                            JOptionPane.showMessageDialog(frame, "Failed to delete subject.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(frame, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a subject to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
