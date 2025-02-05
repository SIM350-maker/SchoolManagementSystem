
package frontend;

import backend.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class ViewClassForm {

    private JFrame frame;
    private JTable classesTable;
    private DefaultTableModel model;

    public ViewClassForm() {
        frame = new JFrame("View Classes");
        frame.setSize(1000, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        loadClasses();
        frame.setVisible(true);
    }

    public void loadClasses() {
        Vector<Vector<Object>> data = new Vector<>();
        Vector<String> columnNames = new Vector<>();

        columnNames.add("Class ID");
        columnNames.add("Class Name");
        columnNames.add("Teacher Name");
        columnNames.add("Update"); // Column for update button
        columnNames.add("Delete"); // Column for delete button

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT class_id, class_name, CONCAT(t.first_name, ' ', t.last_name) AS teacher_name FROM classes c JOIN teachers t ON c.teacher_id = t.teacher_id");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("class_id"));
                row.add(rs.getString("class_name"));
                row.add(rs.getString("teacher_name"));
                row.add("Update"); // Placeholder for update button
                row.add("Delete"); // Placeholder for delete button
                data.add(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error loading classes: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 4; // Allow clicking only in "Update" and "Delete" columns
            }
        };

        classesTable = new JTable(model);
        classesTable.getColumn("Update").setCellRenderer(new UpdateButtonRenderer());
        classesTable.getColumn("Update").setCellEditor(new UpdateButtonEditor(new JCheckBox(), this));

        classesTable.getColumn("Delete").setCellRenderer(new DeleteButtonRenderer());
        classesTable.getColumn("Delete").setCellEditor(new DeleteButtonEditor(new JCheckBox(), this));

        JScrollPane scrollPane = new JScrollPane(classesTable);
        frame.add(scrollPane, BorderLayout.CENTER);
    }

    public void updateClass(int classId, int rowIndex) {
        String className = (String) model.getValueAt(rowIndex, 1);
        String teacherName = (String) model.getValueAt(rowIndex, 2);

        JTextField classNameField = new JTextField(className);
        JTextField teacherNameField = new JTextField(teacherName);

        Object[] message = {
                "Class Name:", classNameField,
                "Teacher Name:", teacherNameField
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Update Class", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String newClassName = classNameField.getText();
            String newTeacherName = teacherNameField.getText();

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("UPDATE classes SET class_name = ? WHERE class_id = ?")) {
                pstmt.setString(1, newClassName);
                pstmt.setInt(2, classId);

                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    model.setValueAt(newClassName, rowIndex, 1);
                    model.setValueAt(newTeacherName, rowIndex, 2);
                    JOptionPane.showMessageDialog(frame, "Class updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to update class", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(frame, "Error updating class: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void deleteClass(int classId, int rowIndex) {
        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this class?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM classes WHERE class_id = ?")) {
                pstmt.setInt(1, classId);

                int rowsDeleted = pstmt.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(frame, "Class deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    model.removeRow(rowIndex); // Remove the row from the table
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to delete class", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(frame, "Error deleting class: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Renderer for Update button
    class UpdateButtonRenderer extends JButton implements TableCellRenderer {
        public UpdateButtonRenderer() {
            setText("Update");
            setBackground(Color.BLUE);
            setForeground(Color.WHITE);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // Editor for Update button
    class UpdateButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int selectedRow;
        public UpdateButtonEditor(JCheckBox checkBox, ViewClassForm viewClassForm) {
            super(checkBox);
            button = new JButton("Update");
            button.setBackground(Color.BLUE);
            button.setForeground(Color.WHITE);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int classId = (int) classesTable.getValueAt(selectedRow, 0);
                    viewClassForm.updateClass(classId, selectedRow);
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            selectedRow = row;
            return button;
        }
    }

    // Renderer for Delete button
    class DeleteButtonRenderer extends JButton implements TableCellRenderer {
        public DeleteButtonRenderer() {
            setText("Delete");
            setBackground(Color.RED);
            setForeground(Color.WHITE);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // Editor for Delete button
    class DeleteButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int selectedRow;
        public DeleteButtonEditor(JCheckBox checkBox, ViewClassForm viewClassForm) {
            super(checkBox);
            button = new JButton("Delete");
            button.setBackground(Color.RED);
            button.setForeground(Color.WHITE);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int classId = (int) classesTable.getValueAt(selectedRow, 0);
                    viewClassForm.deleteClass(classId, selectedRow);
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            selectedRow = row;
            return button;
        }
    }
}
