package frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GenerateReportForm extends JFrame {
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> reportSelector;
    private JButton generateReportButton;

    public GenerateReportForm() {
        setTitle("Generate Reports");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Report selection dropdown
        String[] reports = {"Student Report", "Teacher Report", "Subject Report", "Timetable Report", 
                             "Class Report", "Exams Report", "Enrollment Report", "Attendance Report", "Users Report"};
        reportSelector = new JComboBox<>(reports);
        generateReportButton = new JButton("Generate Report");

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Select Report: "));
        topPanel.add(reportSelector);
        topPanel.add(generateReportButton);
        add(topPanel, BorderLayout.NORTH);

        // Table setup
        tableModel = new DefaultTableModel();
        reportTable = new JTable(tableModel);
        add(new JScrollPane(reportTable), BorderLayout.CENTER);

        // Button action listener
        generateReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedReport = (String) reportSelector.getSelectedItem();
                generateReport(selectedReport);
            }
        });
    }

    private void generateReport(String reportType) {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        String query = "";

        switch (reportType) {
            case "Student Report":
                query = "SELECT student_id, first_name, last_name, date_of_birth, gender, class_id, email, phone, registration_date FROM students";
                break;
            case "Teacher Report":
                query = "SELECT teacher_id, first_name, last_name, subject, email, phone, hire_date FROM teachers";
                break;
            case "Subject Report":
                query = "SELECT subject_id, subject_name FROM subjects";
                break;
            case "Timetable Report":
                query = "SELECT timetable_id, class_id, subject_id, teacher_id, day_of_week, start_time, end_time FROM timetable";
                break;
            case "Class Report":
                query = "SELECT class_id, class_name, teacher_id FROM classes";
                break;
            case "Exams Report":
                query = "SELECT exam_id, exam_name, subject_id, exam_date, total_marks FROM exams";
                break;
            case "Enrollment Report":
                query = "SELECT enrollment_id, student_id, class_id, enrollment_date FROM enrollments";
                break;
            case "Attendance Report":
                query = "SELECT attendance_id, student_id, class_id, date, status FROM attendance";
                break;
            case "Users Report":
                query = "SELECT user_id, username, role, created_at FROM users";
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid report type selected.");
                return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            int columnCount = rs.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(rs.getMetaData().getColumnName(i));
            }

            while (rs.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = rs.getObject(i);
                }
                tableModel.addRow(rowData);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving data: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GenerateReportForm().setVisible(true));
    }
}




























