public class PayrollRecord {

    private final int payID;
    private final int empID;
    private final String payDate;

    private double earnings;
    private final double fedTax;
    private final double fedMed;
    private final double fedSS;
    private final double stateTax;
    private final double retire401k;
    private final double healthCare;

    public PayrollRecord(int payID, int empID, String payDate,
                         double earnings, double fedTax, double fedMed,
                         double fedSS, double stateTax,
                         double retire401k, double healthCare) {

        this.payID = payID;
        this.empID = empID;
        this.payDate = payDate;
        this.earnings = earnings;
        this.fedTax = fedTax;
        this.fedMed = fedMed;
        this.fedSS = fedSS;
        this.stateTax = stateTax;
        this.retire401k = retire401k;
        this.healthCare = healthCare;
    }

    // Getters

    public int getPayID() { return payID; }
    public int getEmpID() { return empID; }
    public String getPayDate() { return payDate; }
    public double getEarnings() { return earnings; }
    public double getFedTax() { return fedTax; }
    public double getFedMed() { return fedMed; }
    public double getFedSS() { return fedSS; }
    public double getStateTax() { return stateTax; }
    public double getRetire401k() { return retire401k; }
    public double getHealthCare() { return healthCare; }

    public void setEarnings(double earnings) { this.earnings = earnings; }

    public double getNetPay() {
        return earnings - (fedTax + fedMed + fedSS + stateTax + retire401k + healthCare);
    }

    @Override
    public String toString() {
        return String.format(
            "%-6d %-6d %-12s %-10.2f %-8.2f %-8.2f %-8.2f %-10.2f %-10.2f %-10.2f %-10.2f",
            payID,
            empID,
            payDate,
            earnings,
            fedTax,
            fedMed,
            fedSS,
            stateTax,
            retire401k,
            healthCare,
            getNetPay());
        }
}