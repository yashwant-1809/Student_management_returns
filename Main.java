import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static final Scanner input = new Scanner(System.in);

    private static final AttendanceManager attendance = new AttendanceManager("attendance.txt");
    private static final GradeManager grades = new GradeManager("results.txt");
    private static final EnrollmentManager enrollments = new EnrollmentManager("enrollments.txt");
    private static final FeeManager fees = new FeeManager("fees.txt");

    public static void main(String[] args) {
        StudentManagementSystem system = new StudentManagementSystem("students.txt");
        loadAll(system);

        Thread autoSaveThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(30000);
                    saveAll(system);
                    System.out.println("\n[Auto-save] All records saved.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (IOException e) {
                    System.out.println("\n[Auto-save error] " + e.getMessage());
                }
            }
        }, "Student-Auto-Save");
        autoSaveThread.setDaemon(true);
        autoSaveThread.start();

        boolean running = true;
        while (running) {
            showMenu();
            try {
                int choice = readInt("Enter choice: ");
                switch (choice) {
                    case 1 -> addStudent(system, false);
                    case 2 -> addStudent(system, true);
                    case 3 -> system.displayStudents();
                    case 4 -> system.searchStudent(readInt("Enter roll number: ")).displayDetails();
                    case 5 -> {
                        int roll = readInt("Enter roll number: ");
                        String email = readText("Enter new email: ");
                        double marks = readDouble("Enter new overall marks (0-100): ");
                        validateMarks(marks);
                        system.updateStudent(roll, email, marks);
                    }
                    case 6 -> system.deleteStudent(readInt("Enter roll number: "));
                    case 7 -> { saveAll(system); System.out.println("All records saved."); }
                    case 8 -> { loadAll(system); System.out.println("All records loaded."); }

                    case 9 -> markAttendance(system);
                    case 10 -> attendance.displayAttendance(readInt("Enter roll number: "));
                    case 11 -> {
                        int threshold = readInt("Low-attendance threshold percentage (0-100): ");
                        if (threshold < 0 || threshold > 100) throw new IllegalArgumentException("Threshold must be 0-100.");
                        attendance.displayLowAttendance(threshold);
                    }

                    case 12 -> addSubjectResult(system);
                    case 13 -> grades.displayReportCard(readInt("Enter roll number: "));

                    case 14 -> enrollStudent(system);
                    case 15 -> enrollments.displayByCourse(readText("Enter course name: "));
                    case 16 -> enrollments.displayByStudent(readInt("Enter roll number: "));

                    case 17 -> setStudentFee(system);
                    case 18 -> {
                        int roll = readInt("Enter roll number: ");
                        fees.makePayment(roll, readDouble("Enter payment amount: "));
                        fees.displayFee(roll);
                    }
                    case 19 -> fees.displayFee(readInt("Enter roll number: "));
                    case 20 -> fees.displayPendingFees();

                    case 21 -> StudentAnalytics.showAnalytics(system.getStudentsSnapshot());
                    case 0 -> {
                        saveAll(system);
                        running = false;
                        System.out.println("All records saved. Goodbye!");
                    }
                    default -> System.out.println("Invalid choice. Please select a listed option.");
                }
            } catch (StudentNotFoundException | IOException | IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        autoSaveThread.interrupt();
        input.close();
    }

    private static void showMenu() {
        System.out.println("\\n========== STUDENT MANAGEMENT SYSTEM ==========");
        System.out.println("STUDENT RECORDS");
        System.out.println("1. Add regular student");
        System.out.println("2. Add graduate student");
        System.out.println("3. Display all students");
        System.out.println("4. Search student");
        System.out.println("5. Update student email and marks");
        System.out.println("6. Delete student");
        System.out.println("7. Save all records");
        System.out.println("8. Reload all records");
        System.out.println("\\nATTENDANCE");
        System.out.println("9. Mark attendance (present/absent)");
        System.out.println("10. View attendance percentage");
        System.out.println("11. List students below attendance threshold");
        System.out.println("\\nGRADES AND RESULTS");
        System.out.println("12. Add marks for a subject");
        System.out.println("13. Generate report card");
        System.out.println("\\nCOURSE ENROLLMENT");
        System.out.println("14. Enroll student in a course");
        System.out.println("15. List students enrolled in a course");
        System.out.println("16. List courses for a student");
        System.out.println("\\nFEE MANAGEMENT");
        System.out.println("17. Set total fee for a student");
        System.out.println("18. Record a fee payment");
        System.out.println("19. View fee status");
        System.out.println("20. List pending fees");
        System.out.println("\\nANALYTICS");
        System.out.println("21. Show class performance analytics");
        System.out.println("0. Save and exit");
    }

    private static void addStudent(StudentManagementSystem system, boolean graduate)
            throws StudentNotFoundException {
        int roll = readInt("Enter roll number: ");
        String name = readText("Enter name: ");
        int age = readInt("Enter age: ");
        if (age <= 0 || age > 120) throw new IllegalArgumentException("Age must be between 1 and 120.");
        String email = readText("Enter email: ");
        if (!email.contains("@") || email.startsWith("@") || email.endsWith("@"))
            throw new IllegalArgumentException("Please enter a valid email address.");
        String courseName = readText("Enter primary course name: ");
        String department = readText("Enter department: ");
        double marks = readDouble("Enter overall marks (0-100): ");
        validateMarks(marks);
        Course course = new Course(courseName, department);
        Student student = graduate
                ? new GraduateStudent(roll, name, age, email, course, marks, readText("Enter research topic: "))
                : new Student(roll, name, age, email, course, marks);
        system.addStudent(student);
    }

    private static void markAttendance(StudentManagementSystem system) throws StudentNotFoundException {
        int roll = readInt("Enter student roll number: ");
        system.searchStudent(roll);
        String status = readText("Present or absent? (P/A): ");
        if (status.equalsIgnoreCase("P")) attendance.markAttendance(roll, true);
        else if (status.equalsIgnoreCase("A")) attendance.markAttendance(roll, false);
        else throw new IllegalArgumentException("Enter P for present or A for absent.");
    }

    private static void addSubjectResult(StudentManagementSystem system) throws StudentNotFoundException {
        int roll = readInt("Enter student roll number: ");
        system.searchStudent(roll);
        String subject = readText("Enter subject name: ");
        double marks = readDouble("Enter subject marks (0-100): ");
        validateMarks(marks);
        grades.addResult(roll, subject, marks);
    }

    private static void enrollStudent(StudentManagementSystem system) throws StudentNotFoundException {
        int roll = readInt("Enter student roll number: ");
        system.searchStudent(roll);
        enrollments.enroll(roll, readText("Enter course name to enroll in: "));
    }

    private static void setStudentFee(StudentManagementSystem system) throws StudentNotFoundException {
        int roll = readInt("Enter student roll number: ");
        system.searchStudent(roll);
        double amount = readDouble("Enter total fee amount: ");
        fees.setFee(roll, amount);
    }

    private static void validateMarks(double marks) {
        if (!Double.isFinite(marks) || marks < 0 || marks > 100)
            throw new IllegalArgumentException("Marks must be between 0 and 100.");
    }

    private static String readText(String prompt) {
        System.out.print(prompt);
        String value = input.nextLine().trim();
        if (value.isEmpty()) throw new IllegalArgumentException("This field cannot be empty.");
        return value;
    }

    private static int readInt(String prompt) {
        System.out.print(prompt);
        try { return Integer.parseInt(input.nextLine().trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Please enter a whole number."); }
    }

    private static double readDouble(String prompt) {
        System.out.print(prompt);
        try { return Double.parseDouble(input.nextLine().trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Please enter a valid number."); }
    }

    private static void saveAll(StudentManagementSystem system) throws IOException {
        system.saveToFile();
        attendance.save();
        grades.save();
        enrollments.save();
        fees.save();
    }

    private static void loadAll(StudentManagementSystem system) {
        try {
            system.loadFromFile();
            attendance.load();
            grades.load();
            enrollments.load();
            fees.load();
            System.out.println("Loaded " + system.getStudentsSnapshot().size() + " student record(s).");
        } catch (IOException e) {
            System.out.println("Could not load all records: " + e.getMessage());
        }
    }
}
