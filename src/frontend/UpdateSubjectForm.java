package frontend;

import backend.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateSubjectForm {
    private JFrame frame;
    private JTextField subjectNameField;
    private JButton updateButton, cancelButton;
    private int subjectId;

    
    public UpdateSubjectForm(int subjectId, String subjectName) {
        this.subjectId = subjectId;

        // Initialize the frame
        frame = new JFrame("Update Subject");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(3, 2, 5, 5));

        // Create and add components
        frame.add(new JLabel("Subject Name:"));
        subjectNameField = new JTextField(subjectName);
        frame.add(subjectNameField);

        updateButton = new JButton("Update");
        cancelButton = new JButton("Cancel");

        frame.add(updateButton);
        frame.add(cancelButton);

        // Button actions
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSubject();  // Update subject in the database
            }
        });

        cancelButton.addActionListener(_ -> frame.dispose());  // Close the form

        // Display the frame
        frame.setVisible(true);
    }

    private void updateSubject() {
        String subjectName = subjectNameField.getText().trim();

        // Validation check
        if (subjectName.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Subject name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE subjects SET subject_name = ? WHERE subject_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, subjectName);
                pstmt.setInt(2, subjectId);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(frame, "Subject updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();  // Close the update form
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to update subject.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
