public class AttendanceRecord {
    private int totalClasses;
    private int presentClasses;

    public void mark(boolean present) {
        totalClasses++;
        if (present) presentClasses++;
    }
    public int getTotalClasses() { return totalClasses; }
    public int getPresentClasses() { return presentClasses; }
    public double getPercentage() {
        return totalClasses == 0 ? 0.0 : presentClasses * 100.0 / totalClasses;
    }
    // Restore persisted counts without replaying individual sessions.
    public void restore(int total, int present) {
        if (total < 0 || present < 0 || present > total)
            throw new IllegalArgumentException("Invalid attendance counts.");
        totalClasses = total;
        presentClasses = present;
    }
}
