
package frontend;

import javax.swing.*;
import java.awt.*;

public class Main {
    private JFrame frame;

    public Main() {
        // Create the main frame
        frame = new JFrame("School Management System");
        frame.setSize(650, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Load background image
        ImageIcon backgroundIcon = new ImageIcon("src/resources/MainBackground.jpg"); // Ensure the image path is correct
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setLayout(new BorderLayout());
        frame.setContentPane(backgroundLabel);

        // Create the header label
        JLabel headerLabel = new JLabel("Welcome to the School Management System", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 26));
        frame.add(headerLabel, BorderLayout.NORTH);

        // Create the main panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(14, 2, 10, 10));
        panel.setOpaque(false); // Make panel transparent to show background

        // Buttons for different sections
        JButton addStudentButton = new JButton("Add Student");
        JButton viewStudentsButton = new JButton("Manage Students");
        JButton addTeacherButton = new JButton("Add Teacher");
        JButton manageTeachersButton = new JButton("Manage Teachers");
        JButton addSubjectButton = new JButton("Add Subject");
        JButton manageSubjectsButton = new JButton("Manage Subjects");
        JButton addTimetableButton = new JButton("Add Timetable");
        JButton manageTimetableButton = new JButton("Manage Timetable");
        JButton addClassButton = new JButton("Add Class");
        JButton manageClassButton = new JButton("Manage Classes");
        JButton addExamsButton = new JButton("Add Exams");
        JButton manageExamsButton = new JButton("Manage Exams");
        JButton addEnrollmentButton = new JButton("Add Enrollment");
        JButton manageEnrollmentButton = new JButton("Manage Enrollments");
        JButton addAttendanceButton = new JButton("Add Attendance");
        JButton manageAttendanceButton = new JButton("Manage Attendance");
        JButton reportsButton = new JButton("Generate Reports");
        JButton exitButton = new JButton("Exit");
        JButton addUsersButton = new JButton("Add Users");
        JButton manageUsersButton = new JButton("Manage Users");

        // Add buttons to the panel
        panel.add(addStudentButton);
        panel.add(viewStudentsButton);
        panel.add(addTeacherButton);
        panel.add(manageTeachersButton);
        panel.add(addSubjectButton);
        panel.add(manageSubjectsButton);
        panel.add(addTimetableButton);
        panel.add(manageTimetableButton);
        panel.add(addClassButton);
        panel.add(manageClassButton);
        panel.add(addExamsButton);
        panel.add(manageExamsButton);
        panel.add(addEnrollmentButton);
        panel.add(manageEnrollmentButton);
        panel.add(addAttendanceButton);
        panel.add(manageAttendanceButton);
        panel.add(addUsersButton);
        panel.add(manageUsersButton);
        panel.add(reportsButton);
        panel.add(exitButton);

        // Add the panel to the frame
        frame.add(panel, BorderLayout.CENTER);

        // Add action listeners to buttons
        addStudentButton.addActionListener(_-> new AddStudentForm());
        viewStudentsButton.addActionListener(_ -> new ViewStudents());
        addSubjectButton.addActionListener(_ -> new AddSubjectForm());
        addTeacherButton.addActionListener(_ -> new AddTeacherForm());
        manageTeachersButton.addActionListener(_ -> new ViewTeachers());
        manageSubjectsButton.addActionListener(_ -> new ViewSubjects());
        addTimetableButton.addActionListener(_ -> new AddTimetableForm());
        manageTimetableButton.addActionListener(_ -> new ViewTimetableForm());
        addClassButton.addActionListener(_ -> new AddClassForm());
        manageClassButton.addActionListener(_ -> new ViewClassForm());
        addExamsButton.addActionListener(_ -> new AddExamsForm());
        manageExamsButton.addActionListener(_ -> new ViewExamsForm());
        addEnrollmentButton.addActionListener(_ -> new AddEnrollmentForm());
        manageEnrollmentButton.addActionListener(_ -> new ViewEnrollmentForm());
        addAttendanceButton.addActionListener(_ -> new AddAttendanceForm());
        manageAttendanceButton.addActionListener(_ -> new ViewAttendanceForm());
        reportsButton.addActionListener(_ -> new GenerateReportForm().setVisible(true));
        exitButton.addActionListener(_ -> System.exit(0));

        // Add action listener for Add Users button
        addUsersButton.addActionListener(_ -> new AddUsersForm());

        // Add action listener for Manage Users button
        manageUsersButton.addActionListener(_ -> new ViewUsersForm());

        // Display the main frame
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}








