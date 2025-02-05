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

public class ViewAttendanceForm {
    private JFrame frame;
    private JTable attendanceTable;
    private JButton updateButton, deleteButton;

    
    public ViewAttendanceForm() {
        frame = new JFrame("View Attendance");
        frame.setSize(800, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        attendanceTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        updateButton = new JButton("Update Attendance");
        deleteButton = new JButton("Delete Attendance");
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        updateButton.addActionListener(_ -> updateAttendance());
        deleteButton.addActionListener(_ -> deleteAttendance());

        loadAttendance();
        frame.setVisible(true);
    }

    private void loadAttendance() {
        Vector<Vector<Object>> data = new Vector<>();
        Vector<String> columnNames = new Vector<>();

        columnNames.add("Attendance ID");
        columnNames.add("Student ID");
        columnNames.add("Class ID");
        columnNames.add("Date");
        columnNames.add("Status");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM attendance");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("attendance_id"));
                row.add(rs.getInt("student_id"));
                row.add(rs.getInt("class_id"));
                row.add(rs.getDate("date"));
                row.add(rs.getString("status"));
                data.add(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error loading attendance records: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        attendanceTable.setModel(model);
    }

    private void updateAttendance() {
        int selectedRow = attendanceTable.getSelectedRow();
        if (selectedRow != -1) {
            int attendanceId = (int) attendanceTable.getValueAt(selectedRow, 0);
            new UpdateAttendanceForm(attendanceId);
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a record to update", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteAttendance() {
        int selectedRow = attendanceTable.getSelectedRow();
        if (selectedRow != -1) {
            int attendanceId = (int) attendanceTable.getValueAt(selectedRow, 0);
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM attendance WHERE attendance_id = ?")) {

                pstmt.setInt(1, attendanceId);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Attendance record deleted successfully!");
                loadAttendance();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(frame, "Error deleting record: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a record to delete", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
}
