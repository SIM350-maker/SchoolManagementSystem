
package frontend;

import backend.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddSubjectForm {
    private JFrame frame;
    private JTextField subjectNameField;
    private JButton saveButton, cancelButton;

    
    public AddSubjectForm() {
        // Initialize the frame
        frame = new JFrame("Add Subject");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(3, 2, 5, 5));

        // Create and add components
        frame.add(new JLabel("Subject Name:"));
        subjectNameField = new JTextField();
        frame.add(subjectNameField);

        // Buttons for save and cancel
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        frame.add(saveButton);
        frame.add(cancelButton);

        // Add action listeners for the buttons
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addSubject();  // Save subject details to database
            }
        });

        cancelButton.addActionListener(_ -> frame.dispose());  // Close the form

        // Display the frame
        frame.setVisible(true);
    }

    private void addSubject() {
        // Get data from the form fields
        String subjectName = subjectNameField.getText().trim();

        // Validation check for empty subject name
        if (subjectName.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Subject name is required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Establish database connection
            conn = DatabaseConnection.getConnection();

            // Check if the subject already exists in the database
            String checkSQL = "SELECT COUNT(*) FROM subjects WHERE subject_name = ?";
            pstmt = conn.prepareStatement(checkSQL);
            pstmt.setString(1, subjectName);
            var resultSet = pstmt.executeQuery();

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                JOptionPane.showMessageDialog(frame, "This subject already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Insert new subject into the database
            String sql = "INSERT INTO subjects (subject_name) VALUES (?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, subjectName);

            // Execute the update
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(frame, "Subject added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFormFields();  // Clear the form after saving
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to add subject.", "Error", JOptionPane.ERROR_MESSAGE);
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

    private void clearFormFields() {
        // Clear the form field
        subjectNameField.setText("");
    }
}
