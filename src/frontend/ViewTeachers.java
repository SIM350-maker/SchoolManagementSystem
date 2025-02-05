package frontend;

import backend.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class ViewTeachers {

    private JFrame frame;
    private JTable teachersTable;
    private JButton updateButton;

    
    public ViewTeachers() {
        frame = new JFrame("View Teachers");
        frame.setSize(900, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        teachersTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(teachersTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        updateButton = new JButton("Update Teacher");
        frame.add(updateButton, BorderLayout.SOUTH);
        updateButton.addActionListener(_ -> updateTeacher());

        loadTeachers();
        frame.setVisible(true);
    }

    private void loadTeachers() {
        Vector<Vector<Object>> data = new Vector<>();
        Vector<String> columnNames = new Vector<>();

        columnNames.add("Teacher ID");
        columnNames.add("First Name");
        columnNames.add("Last Name");
        columnNames.add("Subject");
        columnNames.add("Email");
        columnNames.add("Phone");
        columnNames.add("Hire Date");
        columnNames.add("Actions");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT teacher_id, first_name, last_name, subject, email, phone, hire_date FROM teachers");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("teacher_id"));
                row.add(rs.getString("first_name"));
                row.add(rs.getString("last_name"));
                row.add(rs.getString("subject"));
                row.add(rs.getString("email"));
                row.add(rs.getString("phone"));
                row.add(rs.getDate("hire_date"));
                row.add("Delete");
                data.add(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error loading teachers: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };
        teachersTable.setModel(model);
        teachersTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        teachersTable.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox()));
    }

    private void deleteTeacher(int teacherId) {
        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this teacher?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM teachers WHERE teacher_id = ?")) {
            pstmt.setInt(1, teacherId);
            int rowsDeleted = pstmt.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(frame, "Teacher deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTeachers();
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to delete teacher.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTeacher() {
        int selectedRow = teachersTable.getSelectedRow();
        if (selectedRow != -1) {
            int teacherId = (int) teachersTable.getValueAt(selectedRow, 0);
            new UpdateTeacherForm(teacherId);
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a teacher to update", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("Delete");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int teacherId;
        private boolean clicked;

        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Delete");
            button.addActionListener(_ -> {
                if (clicked) {
                    deleteTeacher(teacherId);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            teacherId = (int) table.getValueAt(row, 0);
            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            clicked = false;
            return "Delete";
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }
}
