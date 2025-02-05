package frontend;

import backend.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddFeeForm {
    private JFrame frame;
    private JTextField studentIdField, amountField, dueDateField, paymentDateField;
    private JComboBox<String> statusComboBox;
    private JButton addButton;

    
    public AddFeeForm() {
        frame = new JFrame("Add Fee");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(6, 2, 10, 10));

        // Labels and Input Fields
        frame.add(new JLabel("Student ID:"));
        studentIdField = new JTextField();
        frame.add(studentIdField);

        frame.add(new JLabel("Amount:"));
        amountField = new JTextField();
        frame.add(amountField);

        frame.add(new JLabel("Status:"));
        statusComboBox = new JComboBox<>(new String[]{"Paid", "Pending"});
        frame.add(statusComboBox);

        frame.add(new JLabel("Due Date (YYYY-MM-DD):"));
        dueDateField = new JTextField();
        frame.add(dueDateField);

        frame.add(new JLabel("Payment Date (YYYY-MM-DD):"));
        paymentDateField = new JTextField();
        frame.add(paymentDateField);

        // Add Button
        addButton = new JButton("Add Fee");
        frame.add(addButton);

        addButton.addActionListener(_ -> addFee());

        frame.setVisible(true);
    }

    private void addFee() {
        String studentId = studentIdField.getText();
        String amount = amountField.getText();
        String status = (String) statusComboBox.getSelectedItem();
        String dueDate = dueDateField.getText();
        String paymentDate = paymentDateField.getText();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO fees (student_id, amount, status, due_date, payment_date) VALUES (?, ?, ?, ?, ?)")) {
            pstmt.setInt(1, Integer.parseInt(studentId));
            pstmt.setDouble(2, Double.parseDouble(amount));
            pstmt.setString(3, status);
            pstmt.setString(4, dueDate);
            pstmt.setString(5, paymentDate.isEmpty() ? null : paymentDate);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Fee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error adding fee: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
