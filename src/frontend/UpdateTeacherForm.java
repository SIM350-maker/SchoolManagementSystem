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

public class UpdateTeacherForm {
    private JFrame frame;
    private JTextField firstNameField, lastNameField, subjectField, emailField, phoneField;
    private JButton updateButton;
    private int teacherId;

    public UpdateTeacherForm(int teacherId) {
        this.teacherId = teacherId;
        frame = new JFrame("Update Teacher");
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(6, 2));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        frame.add(firstNameField);

        frame.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        frame.add(lastNameField);

        frame.add(new JLabel("Subject:"));
        subjectField = new JTextField();
        frame.add(subjectField);

        frame.add(new JLabel("Email:"));
        emailField = new JTextField();
        frame.add(emailField);

        frame.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        frame.add(phoneField);

        updateButton = new JButton("Update Teacher");
        frame.add(updateButton);

        loadTeacherDetails();

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTeacher();
            }
        });

        frame.setVisible(true);
    }

    private void loadTeacherDetails() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT first_name, last_name, subject, email, phone FROM teachers WHERE teacher_id = ?")) {
            pstmt.setInt(1, teacherId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                firstNameField.setText(rs.getString("first_name"));
                lastNameField.setText(rs.getString("last_name"));
                subjectField.setText(rs.getString("subject"));
                emailField.setText(rs.getString("email"));
                phoneField.setText(rs.getString("phone"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error loading teacher details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTeacher() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE teachers SET first_name = ?, last_name = ?, subject = ?, email = ?, phone = ? WHERE teacher_id = ?")) {
            pstmt.setString(1, firstNameField.getText());
            pstmt.setString(2, lastNameField.getText());
            pstmt.setString(3, subjectField.getText());
            pstmt.setString(4, emailField.getText());
            pstmt.setString(5, phoneField.getText());
            pstmt.setInt(6, teacherId);
            
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(frame, "Teacher updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
