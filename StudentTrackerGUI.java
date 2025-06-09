import javax.swing.*;
import java.awt.*;
//import java.awt.event.*;
import java.util.Map;

public class StudentTrackerGUI extends JFrame {
    private StudentManager manager;
    private JTextField idField, nameField, subjectField, scoreField;
    private JTextArea outputArea;

    public StudentTrackerGUI() {
        manager = new StudentManager();

        setTitle("Student Performance Tracker");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Panel for Form
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        inputPanel.add(new JLabel("Student ID:"));
        idField = new JTextField();
        inputPanel.add(idField);

        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Subject:"));
        subjectField = new JTextField();
        inputPanel.add(subjectField);

        inputPanel.add(new JLabel("Score:"));
        scoreField = new JTextField();
        inputPanel.add(scoreField);

        // Add and Update Buttons (separate)
        JButton addButton = new JButton("Add Student");
        addButton.addActionListener(_ -> addStudent());
        inputPanel.add(addButton);

        JButton updateButton = new JButton("Update Score");
        updateButton.addActionListener(_ -> updateScore());
        inputPanel.add(updateButton);

        JButton showButton = new JButton("Show Student Score");
        showButton.addActionListener(_ -> showStudentScore());
        inputPanel.add(showButton);

        add(inputPanel, BorderLayout.NORTH);

        // Output Area (taller)
        outputArea = new JTextArea(20, 50); // made taller here
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }
    

    private void addStudent() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String subject = subjectField.getText().trim();
        int score;

        try {
            score = Integer.parseInt(scoreField.getText().trim());
        } catch (NumberFormatException ex) {
            showMessage("Invalid score format.");
            return;
        }

        Student student = new Student(id, name);
        student.addScore(subject, score);
        manager.addStudent(student);
        showMessage("Student added successfully.\n\n" + displayStudent(student));
    }

    private void updateScore() {
        String id = idField.getText().trim();
        String subject = subjectField.getText().trim();
        int score;

        try {
            score = Integer.parseInt(scoreField.getText().trim());
        } catch (NumberFormatException ex) {
            showMessage("Invalid score format.");
            return;
        }

        Student student = manager.findStudentById(id);
        if (student != null) {
            student.addScore(subject, score);
            showMessage("Score updated.\n\n" + displayStudent(student));
        } else {
            showMessage("Student not found.");
        }
    }
    private void showStudentScore() {
    String id = idField.getText().trim();

    if (id.isEmpty()) {
        showMessage("Please enter Student ID.");
        return;
    }

    Student student = manager.findStudentById(id);
    if (student != null) {
        showMessage("Student found:\n\n" + displayStudent(student));
    } else {
        showMessage("Student not found.");
    }
}

    private void showMessage(String msg) {
        outputArea.setText(msg);
    }

    private String displayStudent(Student student) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(student.getId()).append("\n");
        sb.append("Name: ").append(student.getName()).append("\n");
        sb.append("Scores:\n");
        for (Map.Entry<String, Integer> entry : student.getSubjectScores().entrySet()) {
            sb.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        sb.append("Average: ").append(String.format("%.2f", student.calculateAverage())).append("\n");
        sb.append("Feedback: ").append(student.generateFeedback());
        return sb.toString();
    }

    public static void main(String[] args) {
        new StudentTrackerGUI();
    }
}
