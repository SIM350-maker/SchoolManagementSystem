
package frontend;

import backend.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddClassForm {
    private JFrame frame;
    private JTextField classNameField;
    private JComboBox<String> teacherComboBox;
    private JButton saveButton, cancelButton;

    public AddClassForm() {
        frame = new JFrame("Add Class");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(3, 2, 5, 5));

        // Create and add components
        frame.add(new JLabel("Class Name:"));
        classNameField = new JTextField();
        frame.add(classNameField);

        // Teacher selection
        frame.add(new JLabel("Teacher:"));
        teacherComboBox = new JComboBox<>();
        loadTeachers();  // Populate teacher combo box
        frame.add(teacherComboBox);

        // Buttons
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        frame.add(saveButton);
        frame.add(cancelButton);

        // Action listeners
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addClass();
            }
        });

        cancelButton.addActionListener(_ -> frame.dispose()); // Close form

        // Display the frame
        frame.setVisible(true);
    }

    private void loadTeachers() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT teacher_id, CONCAT(first_name, ' ', last_name) AS teacher_name FROM teachers";  // Changed table name here
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                teacherComboBox.addItem(rs.getString("teacher_name"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addClass() {
        String className = classNameField.getText().trim();
        String selectedTeacher = (String) teacherComboBox.getSelectedItem();

        if (className.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Class name is required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get teacher_id based on selected teacher
        int teacherId = getTeacherIdByName(selectedTeacher);

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Updated table name from "class" to "classes"
            String sql = "INSERT INTO classes (class_name, teacher_id) VALUES (?, ?)";  // Corrected table name here
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, className);
            pstmt.setInt(2, teacherId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(frame, "Class added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFormFields();
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to add class.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getTeacherIdByName(String teacherName) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Corrected table name here as well
            String query = "SELECT teacher_id FROM teachers WHERE CONCAT(first_name, ' ', last_name) = ?";  // Corrected table name here
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, teacherName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("teacher_id");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error fetching teacher ID: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return -1;  // Return an invalid ID if not found
    }

    private void clearFormFields() {
        classNameField.setText("");
        teacherComboBox.setSelectedIndex(0);
    }
}
