import java.sql.*;

public class ReportManager {

    /**
     * PT-07: View Pay Statement History (General Employee)
     */
    public static void getPayrollHistory(int empID) {
        String query = "SELECT * FROM payroll WHERE empid = ? ORDER BY pay_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, empID);
            ResultSet rs = pstmt.executeQuery();

            boolean found = false;
            System.out.printf("%-6s %-6s %-12s %-10s %-8s %-8s %-8s %-10s %-10s %-10s %-10s\n",
            "PayID", "EmpID", "Date", "Earnings",
            "FedTax", "FedMed", "FedSS",
            "StateTax", "401k", "Health", "NetPay");
            System.out.println("--------------------------------------------------------------------------------------------------------");

            while (rs.next()) {
                found = true;

                PayrollRecord record = new PayrollRecord(
                        rs.getInt("payID"),
                        rs.getInt("empid"),
                        rs.getString("pay_date"),
                        rs.getDouble("earnings"),
                        rs.getDouble("fed_tax"),
                        rs.getDouble("fed_med"),
                        rs.getDouble("fed_SS"),
                        rs.getDouble("state_tax"),
                        rs.getDouble("retire_401k"),
                        rs.getDouble("health_care")
                );

                System.out.println("\n" + record);
            }

            if (!found) {
                System.out.println("No payroll records found.");
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving payroll history: " + e.getMessage());
        }
    }

    /**
     * PT-08: Total Pay for Month by Job Title (HR Admin)
     */
    public static void totalPayByJobTitle(int month, int year) {
        String query =
            "SELECT jt.job_title, SUM(p.earnings) AS total_pay " +
            "FROM payroll p " +
            "JOIN employee_job_titles ejt ON p.empid = ejt.empid " +
            "JOIN job_titles jt ON ejt.job_title_id = jt.job_title_id " +
            "WHERE MONTH(p.pay_date) = ? AND YEAR(p.pay_date) = ? " +
            "GROUP BY jt.job_title";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, month);
            pstmt.setInt(2, year);

            ResultSet rs = pstmt.executeQuery();

            System.out.println("\nTotal Pay by Job Title:");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(rs.getString("job_title") + " → $" +
                                   rs.getDouble("total_pay"));
            }

            if (!found) {
                System.out.println("No payroll data found for the specified month and year.");
            }

        } catch (SQLException e) {
            System.err.println("Error generating report: " + e.getMessage());
        }
    }

    /**
     * PT-08: Total Pay for Month by Division (HR Admin)
     */
    public static void totalPayByDivision(int month, int year) {
        String query =
            "SELECT d.Name, SUM(p.earnings) AS total_pay " +
            "FROM payroll p " +
            "JOIN employee_division ed ON p.empid = ed.empid " +
            "JOIN division d ON ed.div_ID = d.divID " +
            "WHERE MONTH(p.pay_date) = ? AND YEAR(p.pay_date) = ? " +
            "GROUP BY d.Name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, month);
            pstmt.setInt(2, year);

            ResultSet rs = pstmt.executeQuery();

            System.out.println("\nTotal Pay by Division:");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(rs.getString("Name") + " → $" +
                                   rs.getDouble("total_pay"));
            }

            if (!found) {
                System.out.println("No payroll data found for the specified month and year.");
            }

        } catch (SQLException e) {
            System.err.println("Error generating report: " + e.getMessage());
        }
    }

    /**
     * PT-08: New Employee Hires in Date Range (HR Admin)
     */
    public static void newHires(String startDate, String endDate) {
        String query =
            "SELECT empid, Fname, Lname, HireDate " +
            "FROM employees " +
            "WHERE HireDate BETWEEN ? AND ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);

            ResultSet rs = pstmt.executeQuery();

            System.out.println("\nNew Hires:");
            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println(
                    rs.getInt("empid") + " | " +
                    rs.getString("Fname") + " " +
                    rs.getString("Lname") + " | " +
                    rs.getDate("HireDate")
                );
            }

            if (!found) {
                System.out.println("No hires found in this range.");
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving hires: " + e.getMessage());
        }
    }
}