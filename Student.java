//import javax.swing.*;
//import java.awt.BorderLayout;
import java.util.*;
//import java.awt.Font;
//import java.awt.Color;
//import java.awt.GridLayout;
public class Student {
    private String id;
    private String name;
    private HashMap<String, Integer> subjectScores; // <Subject, Marks>

    public Student(String id, String name) {
        this.id = id;
        this.name = name;
        this.subjectScores = new HashMap<>();
    }

    public void addScore(String subject, int score) {
        subjectScores.put(subject, score);
    }

    public double calculateAverage() {
        return subjectScores.values().stream().mapToInt(i -> i).average().orElse(0);
    }

    public String generateFeedback() {
        double avg = calculateAverage();
        if (avg >= 90) return "Excellent!";
        else if (avg >= 75) return "Good Job!";
        else if (avg >= 50) return "Needs Improvement.";
        else return "Poor Performance.";
    }

    public String getId() {
    return id;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, Integer> getSubjectScores() {
        return subjectScores;
    }

}