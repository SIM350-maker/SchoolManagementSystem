package frontend;

import backend.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

import java.sql.*;
import java.util.Vector;

public class ViewStudents {

    private JFrame frame;
    private JTable studentsTable;
    private JButton updateButton;

    
    public ViewStudents() {
        frame = new JFrame("View Students");
        frame.setSize(900, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        studentsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(studentsTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        updateButton = new JButton("Update Student");
        frame.add(updateButton, BorderLayout.SOUTH);
        updateButton.addActionListener(_ -> updateStudent());

        loadStudents();
        frame.setVisible(true);
    }

    private void loadStudents() {
        Vector<Vector<Object>> data = new Vector<>();
        Vector<String> columnNames = new Vector<>();

        columnNames.add("Student ID");
        columnNames.add("First Name");
        columnNames.add("Last Name");
        columnNames.add("Date of Birth");
        columnNames.add("Gender");
        columnNames.add("Class ID");
        columnNames.add("Email");
        columnNames.add("Phone");
        columnNames.add("Registration Date");
        columnNames.add("Actions");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT student_id, first_name, last_name, date_of_birth, gender, class_id, email, phone, registration_date FROM students");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("student_id"));
                row.add(rs.getString("first_name"));
                row.add(rs.getString("last_name"));
                row.add(rs.getDate("date_of_birth"));
                row.add(rs.getString("gender"));
                row.add(rs.getInt("class_id"));
                row.add(rs.getString("email"));
                row.add(rs.getString("phone"));
                row.add(rs.getDate("registration_date"));
                row.add("Delete");
                data.add(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error loading students: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9;
            }
        };
        studentsTable.setModel(model);
        studentsTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        studentsTable.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox()));
    }

    private void deleteStudent(int studentId) {
        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this student?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM students WHERE student_id = ?")) {
            pstmt.setInt(1, studentId);
            int rowsDeleted = pstmt.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(frame, "Student deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadStudents();
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to delete student.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStudent() {
        int selectedRow = studentsTable.getSelectedRow();
        if (selectedRow != -1) {
            int studentId = (int) studentsTable.getValueAt(selectedRow, 0);
            new UpdateStudentForm(studentId);
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a student to update", "Warning", JOptionPane.WARNING_MESSAGE);
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
        private int studentId;
        private boolean clicked;

        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Delete");
            button.addActionListener(_ -> {
                if (clicked) {
                    deleteStudent(studentId);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            studentId = (int) table.getValueAt(row, 0);
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