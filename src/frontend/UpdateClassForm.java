
package frontend;

import backend.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class UpdateClassForm {

    private JFrame frame;
    private JTextField classNameField;
    private JComboBox<String> teacherComboBox;
    private JButton updateButton;
    private int classId;
    private ViewClassForm parentView;

    public UpdateClassForm(int classId, ViewClassForm parentView) {
        this.classId = classId;
        this.parentView = parentView;
        frame = new JFrame("Update Class");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(4, 2));

        JLabel classNameLabel = new JLabel("Class Name:");
        classNameField = new JTextField();
        JLabel teacherLabel = new JLabel("Teacher:");
        teacherComboBox = new JComboBox<>();

        loadTeachers();

        updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateClass();
            }
        });

        frame.add(classNameLabel);
        frame.add(classNameField);
        frame.add(teacherLabel);
        frame.add(teacherComboBox);
        frame.add(new JLabel());  // Empty label for grid alignment
        frame.add(updateButton);

        loadClassDetails();

        frame.setVisible(true);
    }

    private void loadTeachers() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT teacher_id, CONCAT(first_name, ' ', last_name) AS teacher_name FROM teachers")) {

            while (rs.next()) {
                teacherComboBox.addItem(rs.getString("teacher_name"));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error loading teachers: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadClassDetails() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT class_name, teacher_id FROM classes WHERE class_id = ?")) {
            pstmt.setInt(1, classId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                classNameField.setText(rs.getString("class_name"));
                int teacherId = rs.getInt("teacher_id");

                // Set the teacherComboBox to the teacher's name
                for (int i = 0; i < teacherComboBox.getItemCount(); i++) {
                    if (teacherComboBox.getItemAt(i).equals(getTeacherNameById(teacherId))) {
                        teacherComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error loading class details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getTeacherNameById(int teacherId) {
        String teacherName = "";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT CONCAT(first_name, ' ', last_name) AS teacher_name FROM teachers WHERE teacher_id = ?")) {
            pstmt.setInt(1, teacherId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                teacherName = rs.getString("teacher_name");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error fetching teacher name: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return teacherName;
    }

    private void updateClass() {
        String className = classNameField.getText();
        int teacherId = getTeacherIdByName(teacherComboBox.getSelectedItem().toString());

        if (className.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Class Name cannot be empty", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE classes SET class_name = ?, teacher_id = ? WHERE class_id = ?")) {
            pstmt.setString(1, className);
            pstmt.setInt(2, teacherId);
            pstmt.setInt(3, classId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(frame, "Class updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                if (parentView != null) {
                    parentView.loadClasses(); // Refresh the view
                }
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to update class", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error updating class: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getTeacherIdByName(String teacherName) {
        int teacherId = -1;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT teacher_id FROM teachers WHERE CONCAT(first_name, ' ', last_name) = ?")) {
            pstmt.setString(1, teacherName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                teacherId = rs.getInt("teacher_id");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error fetching teacher ID: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return teacherId;
    }
}
