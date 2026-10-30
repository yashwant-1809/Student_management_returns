import java.io.*;
import java.util.*;

public class GradeManager {
    private final Map<Integer, ArrayList<SubjectResult>> results = new TreeMap<>();
    private final String fileName;

    public GradeManager(String fileName) { this.fileName = fileName; }

    public synchronized void addResult(int roll, String subject, double marks) {
        SubjectResult result = new SubjectResult(subject, marks);
        ArrayList<SubjectResult> list = results.computeIfAbsent(roll, k -> new ArrayList<>());
        for (SubjectResult old : list)
            if (old.getSubject().equalsIgnoreCase(result.getSubject()))
                throw new IllegalArgumentException("A result for this subject already exists.");
        list.add(result);
        System.out.println("Subject result saved.");
    }

    public synchronized void displayReportCard(int roll) {
        ArrayList<SubjectResult> list = results.get(roll);
        if (list == null || list.isEmpty()) { System.out.println("No results found for this student."); return; }
        double total = 0;
        System.out.println("\\n========== REPORT CARD: ROLL " + roll + " ==========");
        for (SubjectResult r : list) {
            System.out.printf("%-24s Marks: %6.2f  Grade: %s%n", r.getSubject(), r.getMarks(), r.getGrade());
            total += r.getMarks();
        }
        double average = total / list.size();
        System.out.printf("Total: %.2f / %.2f%nAverage: %.2f%nOverall grade: %s%n",
                total, list.size() * 100.0, average, calculateGrade(average));
    }

    public synchronized void save() throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(fileName))) {
            for (Map.Entry<Integer, ArrayList<SubjectResult>> e : results.entrySet())
                for (SubjectResult r : e.getValue()) {
                    String subject = r.getSubject().replace("|", "/").replace("\\n", " ");
                    w.write(e.getKey() + "|" + subject + "|" + r.getMarks());
                    w.newLine();
                }
        }
    }

    public synchronized void load() throws IOException {
        results.clear();
        File f = new File(fileName);
        if (!f.exists()) return;
        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length != 3) continue;
                try {
                    int roll = Integer.parseInt(p[0]);
                    double marks = Double.parseDouble(p[2]);
                    SubjectResult result = new SubjectResult(p[1], marks);
                    ArrayList<SubjectResult> list = results.computeIfAbsent(roll, k -> new ArrayList<>());
                    boolean duplicate = false;
                    for (SubjectResult old : list)
                        if (old.getSubject().equalsIgnoreCase(result.getSubject())) duplicate = true;
                    if (!duplicate) list.add(result);
                } catch (IllegalArgumentException ex) {
                    System.out.println("Skipping invalid result record.");
                }
            }
        }
    }

    public static String calculateGrade(double marks) {
        if (marks >= 90) return "O";
        if (marks >= 80) return "A+";
        if (marks >= 70) return "A";
        if (marks >= 60) return "B+";
        if (marks >= 50) return "B";
        if (marks >= 40) return "C";
        return "F";
    }
}
