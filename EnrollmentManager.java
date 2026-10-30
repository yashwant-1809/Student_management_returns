import java.io.*;
import java.util.*;

public class EnrollmentManager {
    private final ArrayList<Enrollment> enrollments = new ArrayList<>();
    private final String fileName;
    public EnrollmentManager(String fileName) { this.fileName = fileName; }

    public synchronized void enroll(int roll, String course) {
        Enrollment newEnrollment = new Enrollment(roll, course);
        for (Enrollment e : enrollments)
            if (e.getRollNumber() == roll && e.getCourseName().equalsIgnoreCase(newEnrollment.getCourseName()))
                throw new IllegalArgumentException("Student is already enrolled in this course.");
        enrollments.add(newEnrollment);
        System.out.println("Course enrollment successful.");
    }

    public synchronized void displayByCourse(String course) {
        boolean found = false;
        System.out.println("Enrolled students in " + course + ":");
        for (Enrollment e : enrollments)
            if (e.getCourseName().equalsIgnoreCase(course)) {
                System.out.println("Roll number: " + e.getRollNumber());
                found = true;
            }
        if (!found) System.out.println("No students enrolled in this course.");
    }

    public synchronized void displayByStudent(int roll) {
        boolean found = false;
        System.out.println("Courses for roll " + roll + ":");
        for (Enrollment e : enrollments)
            if (e.getRollNumber() == roll) { System.out.println("- " + e.getCourseName()); found = true; }
        if (!found) System.out.println("No course enrollments found.");
    }

    public synchronized void save() throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(fileName))) {
            for (Enrollment e : enrollments) {
                w.write(e.getRollNumber() + "|" + e.getCourseName().replace("|", "/").replace("\\n", " "));
                w.newLine();
            }
        }
    }

    public synchronized void load() throws IOException {
        enrollments.clear();
        File f = new File(fileName);
        if (!f.exists()) return;
        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] p = line.split("\\|", 2);
                if (p.length != 2) continue;
                try {
                    Enrollment e = new Enrollment(Integer.parseInt(p[0]), p[1]);
                    boolean duplicate = false;
                    for (Enrollment old : enrollments)
                        if (old.getRollNumber() == e.getRollNumber()
                                && old.getCourseName().equalsIgnoreCase(e.getCourseName())) duplicate = true;
                    if (!duplicate) enrollments.add(e);
                } catch (IllegalArgumentException ex) {
                    System.out.println("Skipping invalid enrollment record.");
                }
            }
        }
    }
}
