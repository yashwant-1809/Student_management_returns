import java.io.*;
import java.util.*;

public class FeeManager {
    private final Map<Integer, FeeRecord> fees = new TreeMap<>();
    private final String fileName;
    public FeeManager(String fileName) { this.fileName = fileName; }

    public synchronized void setFee(int roll, double amount) {
        FeeRecord existing = fees.get(roll);
        if (existing != null && existing.getAmountPaid() > 0)
            throw new IllegalArgumentException("Cannot replace a fee record after payment; use the existing record.");
        fees.put(roll, new FeeRecord(amount));
        System.out.println("Fee record saved.");
    }

    public synchronized void makePayment(int roll, double amount) {
        FeeRecord record = fees.get(roll);
        if (record == null) throw new IllegalArgumentException("No fee record exists for this student.");
        record.makePayment(amount);
        System.out.println("Payment recorded.");
    }

    public synchronized void displayFee(int roll) {
        FeeRecord r = fees.get(roll);
        if (r == null) { System.out.println("No fee record found."); return; }
        System.out.printf("Total: %.2f | Paid: %.2f | Pending: %.2f | Status: %s%n",
                r.getTotalFee(), r.getAmountPaid(), r.getPendingFee(), r.getStatus());
    }

    public synchronized void displayPendingFees() {
        boolean found = false;
        for (Map.Entry<Integer, FeeRecord> e : fees.entrySet())
            if (e.getValue().getPendingFee() > 0) {
                System.out.printf("Roll %d | Pending: %.2f%n", e.getKey(), e.getValue().getPendingFee());
                found = true;
            }
        if (!found) System.out.println("No pending fees.");
    }

    public synchronized void save() throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(fileName))) {
            for (Map.Entry<Integer, FeeRecord> e : fees.entrySet()) {
                FeeRecord r = e.getValue();
                w.write(e.getKey() + "|" + r.getTotalFee() + "|" + r.getAmountPaid());
                w.newLine();
            }
        }
    }

    public synchronized void load() throws IOException {
        fees.clear();
        File f = new File(fileName);
        if (!f.exists()) return;
        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length != 3) continue;
                try {
                    int roll = Integer.parseInt(p[0]);
                    double total = Double.parseDouble(p[1]), paid = Double.parseDouble(p[2]);
                    FeeRecord record = new FeeRecord(total);
                    record.restorePaid(paid);
                    fees.put(roll, record);
                } catch (IllegalArgumentException ex) {
                    System.out.println("Skipping invalid fee record.");
                }
            }
        }
    }
}
