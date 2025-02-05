package frontend;

import backend.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateAttendanceForm {
    private JFrame frame;
    private JTextField dateField;
    private JComboBox<String> statusComboBox;
    private JButton updateButton;

    private int attendanceId;

    
    public UpdateAttendanceForm(int attendanceId) {
        this.attendanceId = attendanceId;

        frame = new JFrame("Update Attendance");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(3, 2, 10, 10));

        frame.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField();
        frame.add(dateField);

        frame.add(new JLabel("Status:"));
        statusComboBox = new JComboBox<>(new String[]{"Present", "Absent"});
        frame.add(statusComboBox);

        updateButton = new JButton("Update Attendance");
        frame.add(new JLabel()); // Empty space
        frame.add(updateButton);

        updateButton.addActionListener(_ -> updateAttendance());

        frame.setVisible(true);
    }

    private void updateAttendance() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE attendance SET date = ?, status = ? WHERE attendance_id = ?")) {

            pstmt.setString(1, dateField.getText());
            pstmt.setString(2, statusComboBox.getSelectedItem().toString());
            pstmt.setInt(3, attendanceId);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Attendance record updated successfully!");
            frame.dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
