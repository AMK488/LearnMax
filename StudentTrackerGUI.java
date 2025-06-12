import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class StudentTrackerGUI extends JFrame {
    private StudentManager manager;
    private JTextField idField, nameField, subjectField, scoreField;
    private JTextArea outputArea;
    private StudentGraphChart graphChart;
    private JPanel graphPanel;

    public StudentTrackerGUI() {
        manager = new StudentManager();

        setTitle("LearnMax - Student Performance Tracker");
        setSize(1000, 700); // Made window larger for graph
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create main content panels
        createInputPanel();
        createOutputPanel();
        createGraphPanel();

        setVisible(true);
    }

    private void createInputPanel() {
        // Top Panel for Form
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Student Information"));
        inputPanel.setPreferredSize(new Dimension(1000, 150));

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

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton addButton = new JButton("Add Student");
        addButton.addActionListener(_ -> addStudent());
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        buttonPanel.add(addButton);

        JButton updateButton = new JButton("Update Score");
        updateButton.addActionListener(_ -> updateScore());
        updateButton.setBackground(new Color(52, 152, 219));
        updateButton.setForeground(Color.WHITE);
        updateButton.setFocusPainted(false);
        buttonPanel.add(updateButton);

        JButton showButton = new JButton("Show Student");
        showButton.addActionListener(_ -> showStudentScore());
        showButton.setBackground(new Color(155, 89, 182));
        showButton.setForeground(Color.WHITE);
        showButton.setFocusPainted(false);
        buttonPanel.add(showButton);

        JButton graphButton = new JButton("Show Graph");
        graphButton.addActionListener(_ -> showStudentGraph());
        graphButton.setBackground(new Color(241, 196, 15));
        graphButton.setForeground(Color.WHITE);
        graphButton.setFocusPainted(false);
        buttonPanel.add(graphButton);

        JButton clearButton = new JButton("Clear Fields");
        clearButton.addActionListener(_ -> clearFields());
        clearButton.setBackground(new Color(149, 165, 166));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        buttonPanel.add(clearButton);

        // Add components to input panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
    }

    private void createOutputPanel() {
        // Create tabbed pane for output and graph
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Output Area Tab
        outputArea = new JTextArea(15, 40);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setBackground(new Color(248, 249, 250));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Student Details"));
        
        tabbedPane.addTab("Student Details", scrollPane);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Store reference to tabbed pane for graph
        this.tabbedPane = tabbedPane;
    }
    
    private JTabbedPane tabbedPane; // Add this as instance variable

    private void createGraphPanel() {
        graphPanel = new JPanel(new BorderLayout());
        graphPanel.setBorder(BorderFactory.createTitledBorder("Performance Graph"));
        
        // Initialize with empty chart
        graphChart = new StudentGraphChart(null);
        graphPanel.add(graphChart, BorderLayout.CENTER);
        
        // Add instruction label
        JLabel instructionLabel = new JLabel(
            "<html><center>Enter a Student ID and click 'Show Graph' to display performance chart</center></html>",
            SwingConstants.CENTER
        );
        instructionLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        instructionLabel.setForeground(Color.GRAY);
        graphPanel.add(instructionLabel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Performance Graph", graphPanel);
    }

    private void addStudent() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String subject = subjectField.getText().trim();
        
        if (id.isEmpty() || name.isEmpty() || subject.isEmpty()) {
            showMessage("Please fill in all fields.");
            return;
        }
        
        int score;
        try {
            score = Integer.parseInt(scoreField.getText().trim());
            if (score < 0 || score > 100) {
                showMessage("Score must be between 0 and 100.");
                return;
            }
        } catch (NumberFormatException ex) {
            showMessage("Invalid score format. Please enter a number.");
            return;
        }

        // Check if student already exists
        Student existingStudent = manager.findStudentById(id);
        if (existingStudent != null) {
            showMessage("Student with ID " + id + " already exists. Use 'Update Score' to add more subjects.");
            return;
        }

        Student student = new Student(id, name);
        student.addScore(subject, score);
        manager.addStudent(student);
        showMessage("✓ Student added successfully!\n\n" + displayStudent(student));
        
        // Switch to details tab to show the result
        tabbedPane.setSelectedIndex(0);
    }

    private void updateScore() {
        String id = idField.getText().trim();
        String subject = subjectField.getText().trim();
        
        if (id.isEmpty() || subject.isEmpty()) {
            showMessage("Please enter Student ID and Subject.");
            return;
        }
        
        int score;
        try {
            score = Integer.parseInt(scoreField.getText().trim());
            if (score < 0 || score > 100) {
                showMessage("Score must be between 0 and 100.");
                return;
            }
        } catch (NumberFormatException ex) {
            showMessage("Invalid score format. Please enter a number.");
            return;
        }

        Student student = manager.findStudentById(id);
        if (student != null) {
            student.addScore(subject, score);
            showMessage("✓ Score updated successfully!\n\n" + displayStudent(student));
            
            // Update graph if it's currently showing this student
            if (graphChart.getCurrentStudentId() != null && 
                graphChart.getCurrentStudentId().equals(id)) {
                graphChart.updateStudent(student);
            }
            
            tabbedPane.setSelectedIndex(0);
        } else {
            showMessage("✗ Student not found. Please check the Student ID.");
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
            showMessage("✓ Student found:\n\n" + displayStudent(student));
            tabbedPane.setSelectedIndex(0);
        } else {
            showMessage("✗ Student not found. Please check the Student ID.");
        }
    }

    private void showStudentGraph() {
        String id = idField.getText().trim();

        if (id.isEmpty()) {
            showMessage("Please enter Student ID to show graph.");
            return;
        }

        Student student = manager.findStudentById(id);
        if (student != null) {
            if (student.getSubjectScores().isEmpty()) {
                showMessage("No scores available for this student to display graph.");
                return;
            }
            
            graphChart.updateStudent(student);
            tabbedPane.setSelectedIndex(1); // Switch to graph tab
            
            // Also show text details
            showMessage("Displaying graph for:\n\n" + displayStudent(student));
        } else {
            showMessage("✗ Student not found. Please check the Student ID.");
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        subjectField.setText("");
        scoreField.setText("");
        outputArea.setText("");
    }

    private void showMessage(String msg) {
        outputArea.setText(msg);
    }

    private String displayStudent(Student student) {
        StringBuilder sb = new StringBuilder();
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("  STUDENT PERFORMANCE REPORT\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        
        sb.append("Student ID: ").append(student.getId()).append("\n");
        sb.append("Name: ").append(student.getName()).append("\n\n");
        
        sb.append("SUBJECT SCORES:\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        
        for (Map.Entry<String, Integer> entry : student.getSubjectScores().entrySet()) {
            String subject = entry.getKey();
            int score = entry.getValue();
            String performance = getPerformanceLevel(score);
            
            sb.append(String.format("  %-20s: %3d/100  [%s]\n", 
                                  subject, score, performance));
        }
        
        sb.append("─────────────────────────────────────────────────────────\n");
        sb.append(String.format("Overall Average: %.2f/100\n", student.calculateAverage()));
        sb.append("Overall Feedback: ").append(student.generateFeedback()).append("\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        return sb.toString();
    }

    private String getPerformanceLevel(int score) {
        if (score >= 90) return "Excellent";
        else if (score >= 75) return "Good";
        else if (score >= 50) return "Needs Improvement";
        else return "Poor";
    }

    public static void main(String[] args) {
        new StudentTrackerGUI();
    }
}
