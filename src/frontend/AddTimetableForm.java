package frontend;

import backend.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddTimetableForm {
    private JFrame frame;
    private JComboBox<String> classComboBox, subjectComboBox, teacherComboBox, dayComboBox;
    private JTextField startTimeField, endTimeField;
    private JButton saveButton, cancelButton;

    public AddTimetableForm() {
        // Initialize the frame
        frame = new JFrame("Add Timetable");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(7, 2, 5, 5));

        // Add components for class, subject, teacher, day, start and end time
        frame.add(new JLabel("Class:"));
        classComboBox = new JComboBox<>(new String[]{"Class 1", "Class 2", "Class 3"}); // Example options
        frame.add(classComboBox);

        frame.add(new JLabel("Subject:"));
        subjectComboBox = new JComboBox<>(new String[]{"Mathematics", "English", "Computer Science"}); // Example options
        frame.add(subjectComboBox);

        frame.add(new JLabel("Teacher:"));
        teacherComboBox = new JComboBox<>(new String[]{"Teacher A", "Teacher B", "Teacher C"}); // Example options
        frame.add(teacherComboBox);

        frame.add(new JLabel("Day of Week:"));
        dayComboBox = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"});
        frame.add(dayComboBox);

        frame.add(new JLabel("Start Time (HH:MM):"));
        startTimeField = new JTextField();
        frame.add(startTimeField);

        frame.add(new JLabel("End Time (HH:MM):"));
        endTimeField = new JTextField();
        frame.add(endTimeField);

        // Buttons for save and cancel
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        frame.add(saveButton);
        frame.add(cancelButton);

        // Add action listeners for the buttons
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTimetable();  // Save timetable details to database
            }
        });

        cancelButton.addActionListener(_ -> frame.dispose());  // Close the form

        // Display the frame
        frame.setVisible(true);
    }

    private void addTimetable() {
        // Get data from the form fields
        String selectedClass = classComboBox.getSelectedItem().toString();
        String selectedSubject = subjectComboBox.getSelectedItem().toString();
        String selectedTeacher = teacherComboBox.getSelectedItem().toString();
        String selectedDay = dayComboBox.getSelectedItem().toString();
        String startTime = startTimeField.getText().trim();
        String endTime = endTimeField.getText().trim();

        // Validation check for fields
        if (startTime.isEmpty() || endTime.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "All fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO timetable (class_id, subject_id, teacher_id, day_of_week, start_time, end_time) VALUES (?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            // Assuming the IDs of classes, subjects, and teachers are mapped as follows
            pstmt.setInt(1, getClassId(selectedClass));
            pstmt.setInt(2, getSubjectId(selectedSubject));
            pstmt.setInt(3, getTeacherId(selectedTeacher));
            pstmt.setString(4, selectedDay);
            pstmt.setString(5, startTime);
            pstmt.setString(6, endTime);

            // Execute the update
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(frame, "Timetable added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFormFields();  // Clear the form after saving
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to add timetable.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private int getClassId(String className) {
        // Implement logic to get class ID based on className
        return 1;  // Example return value
    }

    private int getSubjectId(String subjectName) {
        // Implement logic to get subject ID based on subjectName
        return 1;  // Example return value
    }

    private int getTeacherId(String teacherName) {
        // Implement logic to get teacher ID based on teacherName
        return 1;  // Example return value
    }

    private void clearFormFields() {
        classComboBox.setSelectedIndex(0);
        subjectComboBox.setSelectedIndex(0);
        teacherComboBox.setSelectedIndex(0);
        dayComboBox.setSelectedIndex(0);
        startTimeField.setText("");
        endTimeField.setText("");
    }
}
