package frontend;

import backend.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class ViewFeeForm {
    private JFrame frame;
    private JTable feeTable;
    private JButton updateButton, deleteButton;

    
    public ViewFeeForm() {
        frame = new JFrame("Manage Fees");
        frame.setSize(800, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        feeTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(feeTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        updateButton = new JButton("Update Fee");
        deleteButton = new JButton("Delete Fee");
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        updateButton.addActionListener(_ -> updateFee());
        deleteButton.addActionListener(_ -> deleteFee());

        loadFees();
        frame.setVisible(true);
    }

    private void loadFees() {
        Vector<Vector<Object>> data = new Vector<>();
        Vector<String> columnNames = new Vector<>();

        columnNames.add("Fee ID");
        columnNames.add("Student ID");
        columnNames.add("Amount");
        columnNames.add("Status");
        columnNames.add("Due Date");
        columnNames.add("Payment Date");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT fee_id, student_id, amount, status, due_date, payment_date FROM fees");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("fee_id"));
                row.add(rs.getInt("student_id"));
                row.add(rs.getDouble("amount"));
                row.add(rs.getString("status"));
                row.add(rs.getDate("due_date"));
                row.add(rs.getDate("payment_date"));
                data.add(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error loading fees: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        feeTable.setModel(model);
    }

    private void updateFee() {
        int selectedRow = feeTable.getSelectedRow();
        if (selectedRow != -1) {
            int feeId = (int) feeTable.getValueAt(selectedRow, 0);
            new UpdateFeeForm(feeId);
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a fee record to update", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteFee() {
        int selectedRow = feeTable.getSelectedRow();
        if (selectedRow != -1) {
            int feeId = (int) feeTable.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this fee record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement("DELETE FROM fees WHERE fee_id = ?")) {
                    pstmt.setInt(1, feeId);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(frame, "Fee record deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadFees();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error deleting fee: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a fee record to delete", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
}
