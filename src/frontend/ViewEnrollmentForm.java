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

public class ViewEnrollmentForm {
    private JFrame frame;
    private JTable enrollmentTable;
    private JButton updateButton, deleteButton;

    public ViewEnrollmentForm() {
        frame = new JFrame("Manage Enrollments");
        frame.setSize(800, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        enrollmentTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(enrollmentTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        updateButton = new JButton("Update Enrollment");
        deleteButton = new JButton("Delete Enrollment");

        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        updateButton.addActionListener(_ -> updateEnrollment());
        deleteButton.addActionListener(_ -> deleteEnrollment());

        loadEnrollments();
        frame.setVisible(true);
    }

    private void loadEnrollments() {
        Vector<Vector<Object>> data = new Vector<>();
        Vector<String> columnNames = new Vector<>();

        columnNames.add("Enrollment ID");
        columnNames.add("Student ID");
        columnNames.add("Class ID");
        columnNames.add("Enrollment Date");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT enrollment_id, student_id, class_id, enrollment_date FROM enrollments");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("enrollment_id"));
                row.add(rs.getInt("student_id"));
                row.add(rs.getInt("class_id"));
                row.add(rs.getDate("enrollment_date"));
                data.add(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error loading enrollments: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        enrollmentTable.setModel(model);
    }

    private void updateEnrollment() {
        int selectedRow = enrollmentTable.getSelectedRow();
        if (selectedRow != -1) {
            int enrollmentId = (int) enrollmentTable.getValueAt(selectedRow, 0);
            new UpdateEnrollmentForm(enrollmentId);
        } else {
            JOptionPane.showMessageDialog(frame, "Please select an enrollment to update", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteEnrollment() {
        int selectedRow = enrollmentTable.getSelectedRow();
        if (selectedRow != -1) {
            int enrollmentId = (int) enrollmentTable.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this enrollment?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement("DELETE FROM enrollments WHERE enrollment_id = ?")) {

                    pstmt.setInt(1, enrollmentId);
                    int rowsDeleted = pstmt.executeUpdate();

                    if (rowsDeleted > 0) {
                        JOptionPane.showMessageDialog(frame, "Enrollment deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadEnrollments(); // Refresh table
                    } else {
                        JOptionPane.showMessageDialog(frame, "Failed to delete enrollment!", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(frame, "Error deleting enrollment: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please select an enrollment to delete", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
}
