
package frontend;

import backend.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class UpdateTimetableForm {
    private JFrame frame;
    private JTextField classIdField, subjectIdField, teacherIdField, dayOfWeekField, startTimeField, endTimeField;
    private int timetableId;

    
    public UpdateTimetableForm(int timetableId, int classId, int subjectId, int teacherId, String dayOfWeek, String startTime, String endTime) {
        this.timetableId = timetableId;

        // Initialize the frame
        frame = new JFrame("Update Timetable Entry");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(7, 2, 10, 10));

        // Add labels and text fields
        frame.add(new JLabel("Class ID:"));
        classIdField = new JTextField(String.valueOf(classId));
        frame.add(classIdField);

        frame.add(new JLabel("Subject ID:"));
        subjectIdField = new JTextField(String.valueOf(subjectId));
        frame.add(subjectIdField);

        frame.add(new JLabel("Teacher ID:"));
        teacherIdField = new JTextField(String.valueOf(teacherId));
        frame.add(teacherIdField);

        frame.add(new JLabel("Day of Week:"));
        dayOfWeekField = new JTextField(dayOfWeek);
        frame.add(dayOfWeekField);

        frame.add(new JLabel("Start Time:"));
        startTimeField = new JTextField(startTime);
        frame.add(startTimeField);

        frame.add(new JLabel("End Time:"));
        endTimeField = new JTextField(endTime);
        frame.add(endTimeField);

        // Add Save and Cancel buttons
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(_ -> saveTimetableEntry());
        cancelButton.addActionListener(_ -> frame.dispose());

        frame.add(saveButton);
        frame.add(cancelButton);

        // Display the frame
        frame.setVisible(true);
    }

    private void saveTimetableEntry() {
        // Get the values from the fields
        int classId = Integer.parseInt(classIdField.getText());
        int subjectId = Integer.parseInt(subjectIdField.getText());
        int teacherId = Integer.parseInt(teacherIdField.getText());
        String dayOfWeek = dayOfWeekField.getText();
        String startTime = startTimeField.getText();
        String endTime = endTimeField.getText();

        String sql = "UPDATE timetable SET class_id = ?, subject_id = ?, teacher_id = ?, day_of_week = ?, start_time = ?, end_time = ? WHERE timetable_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, classId);
            pstmt.setInt(2, subjectId);
            pstmt.setInt(3, teacherId);
            pstmt.setString(4, dayOfWeek);
            pstmt.setString(5, startTime);
            pstmt.setString(6, endTime);
            pstmt.setInt(7, timetableId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(frame, "Timetable entry updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();  // Close the window after saving
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to update timetable entry.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error updating timetable entry: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

