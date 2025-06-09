//import javax.swing.*;
//import java.awt.BorderLayout;
import java.util.*;
//import java.awt.Font;
//import java.awt.Color;
//import java.awt.GridLayout;public class StudentManager {
public class StudentManager {
    private ArrayList<Student> students;

    public StudentManager() {
        students = new ArrayList<>();
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public Student findStudentById(String id) {
        for (Student s : students) {
            if (s.getId().equals(id)) return s;
        }
        return null;
    }

    public ArrayList<Student> getAllStudents() {
        return students;
    }
}