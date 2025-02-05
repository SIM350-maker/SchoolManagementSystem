
package frontend;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class ViewUsersForm extends JFrame {
    private JTable usersTable;
    private JButton deleteButton, updateButton;

    public ViewUsersForm() {
        setTitle("View Users");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create a panel to hold the table and buttons
        JPanel panel = new JPanel(new BorderLayout());
        add(panel);

        // Fetch and display users in a JTable
        String[] columnNames = {"User ID", "Username", "Role", "Created At"};
        ArrayList<Object[]> userData = getUsersData();

        usersTable = new JTable(userData.toArray(new Object[0][]), columnNames);
        JScrollPane scrollPane = new JScrollPane(usersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Create buttons for Delete and Update actions
        JPanel buttonPanel = new JPanel();
        panel.add(buttonPanel, BorderLayout.SOUTH);

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(_ -> deleteUser());
        buttonPanel.add(deleteButton);

        updateButton = new JButton("Update");
        updateButton.addActionListener(_ -> updateUser());
        buttonPanel.add(updateButton);

        setVisible(true);
    }

    private ArrayList<Object[]> getUsersData() {
        ArrayList<Object[]> data = new ArrayList<>();
        String query = "SELECT user_id, username, role, created_at FROM users";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                data.add(new Object[]{
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getTimestamp("created_at")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving users: " + e.getMessage());
        }

        return data;
    }

    private void deleteUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow != -1) {
            int userId = (int) usersTable.getValueAt(selectedRow, 0);

            int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this user?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                String query = "DELETE FROM users WHERE user_id = ?";

                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {

                    stmt.setInt(1, userId);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "User deleted successfully.");
                    refreshTable(); // Refresh the table after deletion

                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
        }
    }

    private void updateUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow != -1) {
            int userId = (int) usersTable.getValueAt(selectedRow, 0);
            // Open a new window for updating user details (create an UpdateUsersForm class for this purpose)
            new UpdateUsersForm(userId);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to update.");
        }
    }

    private void refreshTable() {
        // Refresh the data in the JTable
        ArrayList<Object[]> userData = getUsersData();
        usersTable.setModel(new javax.swing.table.DefaultTableModel(
            userData.toArray(new Object[0][]),
            new String[]{"User ID", "Username", "Role", "Created At"}
        ));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewUsersForm::new);
    }
}
