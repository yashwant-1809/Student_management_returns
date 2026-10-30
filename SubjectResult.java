public class SubjectResult {
    private final String subject;
    private final double marks;

    public SubjectResult(String subject, double marks) {
        if (subject == null || subject.trim().isEmpty())
            throw new IllegalArgumentException("Subject name cannot be empty.");
        if (marks < 0 || marks > 100)
            throw new IllegalArgumentException("Marks must be between 0 and 100.");
        this.subject = subject.trim();
        this.marks = marks;
    }
    public String getSubject() { return subject; }
    public double getMarks() { return marks; }
    public String getGrade() { return GradeManager.calculateGrade(marks); }
}
