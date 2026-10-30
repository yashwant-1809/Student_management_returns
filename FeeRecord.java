public class FeeRecord {
    private final double totalFee;
    private double amountPaid;

    public FeeRecord(double totalFee) {
        if (!Double.isFinite(totalFee) || totalFee < 0)
            throw new IllegalArgumentException("Total fee must be a valid non-negative amount.");
        this.totalFee = totalFee;
    }
    public double getTotalFee() { return totalFee; }
    public double getAmountPaid() { return amountPaid; }
    public double getPendingFee() { return totalFee - amountPaid; }
    public String getStatus() {
        if (amountPaid == 0 && totalFee > 0) return "Pending";
        if (amountPaid < totalFee) return "Partially Paid";
        return "Paid";
    }
    public void makePayment(double amount) {
        if (!Double.isFinite(amount) || amount <= 0)
            throw new IllegalArgumentException("Payment must be greater than zero.");
        if (amount > getPendingFee())
            throw new IllegalArgumentException("Payment exceeds the pending fee.");
        amountPaid += amount;
    }
    public void restorePaid(double paid) {
        if (paid < 0 || paid > totalFee) throw new IllegalArgumentException("Invalid paid amount.");
        amountPaid = paid;
    }
}
