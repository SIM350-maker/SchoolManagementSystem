package frontend;

import backend.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class AddAttendanceForm {
    private JFrame frame;
    private JTextField studentIdField, classIdField, dateField;
    private JComboBox<String> statusComboBox;
    private JButton addButton;
    
    public AddAttendanceForm() {

        frame = new JFrame("Add Attendance");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(5, 2, 10, 10));

        frame.add(new JLabel("Student ID:"));
        studentIdField = new JTextField();
        frame.add(studentIdField);

        frame.add(new JLabel("Class ID:"));
        classIdField = new JTextField();
        frame.add(classIdField);

        frame.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField();
        frame.add(dateField);

        frame.add(new JLabel("Status:"));
        statusComboBox = new JComboBox<>(new String[]{"Present", "Absent"});
        frame.add(statusComboBox);

        addButton = new JButton("Add Attendance");
        frame.add(new JLabel()); // Empty space
        frame.add(addButton);

        addButton.addActionListener(_ -> addAttendance());

        frame.setVisible(true);
    }

    private void addAttendance() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO attendance (student_id, class_id, date, status) VALUES (?, ?, ?, ?)")) {

            pstmt.setInt(1, Integer.parseInt(studentIdField.getText()));
            pstmt.setInt(2, Integer.parseInt(classIdField.getText()));
            pstmt.setString(3, dateField.getText());
            pstmt.setString(4, statusComboBox.getSelectedItem().toString());

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Attendance record added successfully!");
            frame.dispose();

        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
