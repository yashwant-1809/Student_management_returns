import java.io.*;
import java.util.*;

public class AttendanceManager {
    private final Map<Integer, AttendanceRecord> records = new TreeMap<>();
    private final String fileName;

    public AttendanceManager(String fileName) { this.fileName = fileName; }

    public synchronized void markAttendance(int roll, boolean present) {
        records.computeIfAbsent(roll, k -> new AttendanceRecord()).mark(present);
        System.out.println("Attendance marked as " + (present ? "PRESENT." : "ABSENT."));
    }

    public synchronized void displayAttendance(int roll) {
        AttendanceRecord r = records.get(roll);
        if (r == null) { System.out.println("No attendance records for this roll number."); return; }
        System.out.printf("Classes: %d | Present: %d | Attendance: %.2f%%%n",
                r.getTotalClasses(), r.getPresentClasses(), r.getPercentage());
    }

    public synchronized void displayLowAttendance(int threshold) {
        boolean found = false;
        for (Map.Entry<Integer, AttendanceRecord> e : records.entrySet()) {
            if (e.getValue().getPercentage() < threshold) {
                System.out.printf("Roll %d: %.2f%%%n", e.getKey(), e.getValue().getPercentage());
                found = true;
            }
        }
        if (!found) System.out.println("No students are below " + threshold + "% attendance.");
    }

    public synchronized void save() throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(fileName))) {
            for (Map.Entry<Integer, AttendanceRecord> e : records.entrySet()) {
                AttendanceRecord r = e.getValue();
                w.write(e.getKey() + "|" + r.getTotalClasses() + "|" + r.getPresentClasses());
                w.newLine();
            }
        }
    }

    public synchronized void load() throws IOException {
        records.clear();
        File f = new File(fileName);
        if (!f.exists()) return;
        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length != 3) continue;
                try {
                    int roll = Integer.parseInt(p[0]), total = Integer.parseInt(p[1]);
                    int present = Integer.parseInt(p[2]);
                    AttendanceRecord record = new AttendanceRecord();
                    record.restore(total, present);
                    records.put(roll, record);
                } catch (IllegalArgumentException ex) {
                    System.out.println("Skipping invalid attendance record.");
                }
            }
        }
    }
}
