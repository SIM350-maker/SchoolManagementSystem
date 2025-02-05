package frontend;

import backend.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewTimetableForm {
    private JFrame frame;
    private JTable timetableTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton editButton, deleteButton, refreshButton;

    
    public ViewTimetableForm() {
        // Initialize the frame
        frame = new JFrame("Manage Timetable");
        frame.setSize(800, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Create the header label
        JLabel headerLabel = new JLabel("Timetable Management", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        frame.add(headerLabel, BorderLayout.NORTH);

        // Create the table to display timetable
        String[] columnNames = {"Timetable ID", "Class ID", "Subject ID", "Teacher ID", "Day", "Start Time", "End Time"};
        tableModel = new DefaultTableModel(columnNames, 0);
        timetableTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(timetableTable);
        frame.add(tableScrollPane, BorderLayout.CENTER);

        // Panel for search and buttons
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        searchField = new JTextField(20);
        searchField.setToolTipText("Search by class, subject, or teacher");
        panel.add(new JLabel("Search:"));
        panel.add(searchField);

        refreshButton = new JButton("Refresh");
        panel.add(refreshButton);

        editButton = new JButton("Edit");
        panel.add(editButton);

        deleteButton = new JButton("Delete");
        panel.add(deleteButton);

        frame.add(panel, BorderLayout.SOUTH);

        // Action listeners
        refreshButton.addActionListener(_ -> loadTimetableData());
        editButton.addActionListener(_ -> editTimetable());
        deleteButton.addActionListener(_ -> deleteTimetable());
        searchField.addActionListener(_ -> searchTimetable());

        // Load initial data
        loadTimetableData();

        // Display the frame
        frame.setVisible(true);
    }

    private void loadTimetableData() {
        // Clear the existing table data
        tableModel.setRowCount(0);

        String sql = "SELECT timetable_id, class_id, subject_id, teacher_id, day_of_week, start_time, end_time FROM timetable";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int timetableId = rs.getInt("timetable_id");
                int classId = rs.getInt("class_id");
                int subjectId = rs.getInt("subject_id");
                int teacherId = rs.getInt("teacher_id");
                String day = rs.getString("day_of_week");
                String startTime = rs.getString("start_time");
                String endTime = rs.getString("end_time");

                // Add data to the table model
                Object[] row = {timetableId, classId, subjectId, teacherId, day, startTime, endTime};
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error loading timetable data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchTimetable() {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            loadTimetableData();
            return;
        }

        String sql = "SELECT timetable_id, class_id, subject_id, teacher_id, day_of_week, start_time, end_time FROM timetable WHERE class_id LIKE ? OR subject_id LIKE ? OR teacher_id LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + searchText + "%");
            pstmt.setString(2, "%" + searchText + "%");
            pstmt.setString(3, "%" + searchText + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                tableModel.setRowCount(0);  // Clear existing rows

                while (rs.next()) {
                    int timetableId = rs.getInt("timetable_id");
                    int classId = rs.getInt("class_id");
                    int subjectId = rs.getInt("subject_id");
                    int teacherId = rs.getInt("teacher_id");
                    String day = rs.getString("day_of_week");
                    String startTime = rs.getString("start_time");
                    String endTime = rs.getString("end_time");

                    // Add data to the table model
                    Object[] row = {timetableId, classId, subjectId, teacherId, day, startTime, endTime};
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error searching timetable data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editTimetable() {
        int selectedRow = timetableTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a timetable entry to edit.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int timetableId = (int) tableModel.getValueAt(selectedRow, 0);
        int classId = (int) tableModel.getValueAt(selectedRow, 1);
        int subjectId = (int) tableModel.getValueAt(selectedRow, 2);
        int teacherId = (int) tableModel.getValueAt(selectedRow, 3);
        String day = (String) tableModel.getValueAt(selectedRow, 4);
        String startTime = (String) tableModel.getValueAt(selectedRow, 5);
        String endTime = (String) tableModel.getValueAt(selectedRow, 6);

        // Open the update form with pre-filled data
        new UpdateTimetableForm(timetableId, classId, subjectId, teacherId, day, startTime, endTime);
    }

    private void deleteTimetable() {
        int selectedRow = timetableTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a timetable entry to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int timetableId = (int) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this timetable entry?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM timetable WHERE timetable_id = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, timetableId);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(frame, "Timetable entry deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadTimetableData();  // Refresh the table data
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to delete timetable entry.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error deleting timetable entry: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
