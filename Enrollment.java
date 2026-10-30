public class Enrollment {
    private final int rollNumber;
    private final String courseName;
    public Enrollment(int rollNumber, String courseName) {
        if (courseName == null || courseName.trim().isEmpty())
            throw new IllegalArgumentException("Course name cannot be empty.");
        this.rollNumber = rollNumber;
        this.courseName = courseName.trim();
    }
    public int getRollNumber() { return rollNumber; }
    public String getCourseName() { return courseName; }
}
