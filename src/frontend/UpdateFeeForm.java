package frontend;

import backend.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateFeeForm {
    private JFrame frame;
    private JTextField amountField, dueDateField, paymentDateField;
    private JComboBox<String> statusComboBox;
    private JButton updateButton, deleteButton;
    private int feeId; // ID of the fee record being updated

    
    public UpdateFeeForm(int feeId) {
        this.feeId = feeId;

        frame = new JFrame("Update Fee Record");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(6, 2, 10, 10));

        // Labels and input fields
        frame.add(new JLabel("Amount:"));
        amountField = new JTextField();
        frame.add(amountField);

        frame.add(new JLabel("Status:"));
        String[] statuses = {"Pending", "Paid", "Overdue"};
        statusComboBox = new JComboBox<>(statuses);
        frame.add(statusComboBox);

        frame.add(new JLabel("Due Date (YYYY-MM-DD):"));
        dueDateField = new JTextField();
        frame.add(dueDateField);

        frame.add(new JLabel("Payment Date (YYYY-MM-DD):"));
        paymentDateField = new JTextField();
        frame.add(paymentDateField);

        // Buttons
        updateButton = new JButton("Update Fee");
        deleteButton = new JButton("Delete Fee");

        frame.add(updateButton);
        frame.add(deleteButton);

        // Load fee details
        loadFeeDetails();

        // Button actions
        updateButton.addActionListener(_ -> updateFeeRecord());
        deleteButton.addActionListener(_ -> deleteFeeRecord());

        frame.setVisible(true);
    }

    private void loadFeeDetails() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT amount, status, due_date, payment_date FROM fees WHERE fee_id = ?")) {
            pstmt.setInt(1, feeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                amountField.setText(String.valueOf(rs.getInt("amount")));
                statusComboBox.setSelectedItem(rs.getString("status"));
                dueDateField.setText(rs.getString("due_date"));
                paymentDateField.setText(rs.getString("payment_date"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error loading fee details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateFeeRecord() {
        int amount = Integer.parseInt(amountField.getText().trim());
        String status = (String) statusComboBox.getSelectedItem();
        String dueDate = dueDateField.getText().trim();
        String paymentDate = paymentDateField.getText().trim();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE fees SET amount = ?, status = ?, due_date = ?, payment_date = ? WHERE fee_id = ?")) {
            pstmt.setInt(1, amount);
            pstmt.setString(2, status);
            pstmt.setString(3, dueDate);
            pstmt.setString(4, paymentDate);
            pstmt.setInt(5, feeId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(frame, "Fee record updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "No changes were made.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error updating fee: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteFeeRecord() {
        int confirmation = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this fee record?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM fees WHERE fee_id = ?")) {
                pstmt.setInt(1, feeId);
                int rowsDeleted = pstmt.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(frame, "Fee record deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "Error deleting fee record.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
