import java.util.*;

public class StudentAnalytics {
    public static void showAnalytics(ArrayList<Student> students) {
        if (students.isEmpty()) { System.out.println("No student records available."); return; }
        double total = 0;
        Student topper = students.get(0);
        ArrayList<Student> sorted = new ArrayList<>(students);
        for (Student s : students) {
            total += s.getMarks();
            if (s.getMarks() > topper.getMarks()) topper = s;
        }
        sorted.sort(Comparator.comparingDouble(Student::getMarks).reversed());
        System.out.printf("Number of students: %d%nClass average (existing overall marks): %.2f%n",
                students.size(), total / students.size());
        System.out.printf("Topper: %s (Roll %d, Marks %.2f)%n",
                topper.getName(), topper.getRollNumber(), topper.getMarks());
        System.out.println("\\nRankings:");
        int rank = 1;
        for (Student s : sorted) {
            System.out.printf("%d. %-20s Roll: %-6d Marks: %6.2f Status: %s%n",
                    rank++, s.getName(), s.getRollNumber(), s.getMarks(),
                    s.getMarks() >= 40 ? "Pass" : "Fail");
        }
    }
}
