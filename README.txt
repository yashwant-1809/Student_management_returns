# Student Management System (Java)

## Requirements
- JDK 17 or newer recommended.
- No external libraries required.

## Run
Open a terminal in this folder and run:
```bash
javac *.java
java Main
```

## Added modules
1. Attendance: mark present/absent, view percentage, list students below a threshold.
2. Grades/results: add marks by subject and generate a report card with totals, average and grade.
3. Course enrollment: enroll a student, prevent duplicate enrollment, list enrollment by course or student.
4. Fees: set total fee, record payments, view paid/pending balance and list pending fees.
5. Analytics: class average, topper, rankings and pass/fail based on each student's existing overall marks.

## Data files
The program creates these files in the working directory:
- `students.txt`
- `attendance.txt`
- `results.txt`
- `enrollments.txt`
- `fees.txt`

Use menu option 7 or exit with option 0 to save all records. Auto-save also runs every 30 seconds.

## Notes
- Attendance is counted by marking each class session present or absent.
- Subject report cards are separate from the student's existing overall marks field.
- Grade thresholds are: O (90+), A+ (80-89.99), A (70-79.99), B+ (60-69.99), B (50-59.99), C (40-49.99), F (below 40).
- The fee module is a simple academic demonstration, not a payment gateway.
