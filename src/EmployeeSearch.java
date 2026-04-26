import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class EmployeeSearch {
    public static void searchEmployeeData(Scanner scanner) {
        System.out.println("\nSearch Employee Data (View Only)");
        System.out.println("Search by:");
        System.out.println("1. Employee ID");
        System.out.println("2. Name");
        System.out.println("3. Date of Birth (YYYY-MM-DD)");
        System.out.println("4. SSN");
        System.out.print("Select option (1-4): ");

        String option = scanner.nextLine().trim();
        String sql;
        String searchValue;
        boolean useLike = false;
        Integer empId = null;

        switch (option) {
            case "1" -> {
                System.out.print("Enter Employee ID: ");
                searchValue = scanner.nextLine().trim();
                if (searchValue.isEmpty()) {
                    System.out.println("Employee ID cannot be empty.");
                    return;
                }
                try {
                    empId = Integer.parseInt(searchValue);
                } catch (NumberFormatException e) {
                    System.out.println("Employee ID must be a number.");
                    return;
                }
                sql = baseSearchQuery() + " WHERE e.empid = ? GROUP BY "
                    + "e.empid, e.Fname, e.Lname, e.SSN, e.email, e.HireDate, e.Salary, "
                    + "d.DOB, d.phone, d.emergency_contact_name, d.emergency_contact_phone";
            }
            case "2" -> {
                System.out.print("Enter full or partial name: ");
                searchValue = scanner.nextLine().trim().toLowerCase();
                if (searchValue.isEmpty()) {
                    System.out.println("Name search text cannot be empty.");
                    return;
                }
                useLike = true;
                sql = baseSearchQuery() + " WHERE LOWER(e.Fname) LIKE ? "
                    + "OR LOWER(e.Lname) LIKE ? "
                    + "OR LOWER(CONCAT(e.Fname, ' ', e.Lname)) LIKE ? GROUP BY "
                    + "e.empid, e.Fname, e.Lname, e.SSN, e.email, e.HireDate, e.Salary, "
                    + "d.DOB, d.phone, d.emergency_contact_name, d.emergency_contact_phone";
                searchValue = "%" + searchValue + "%";
            }
            case "3" -> {
                System.out.print("Enter DOB (YYYY-MM-DD): ");
                searchValue = scanner.nextLine().trim();
                if (searchValue.isEmpty()) {
                    System.out.println("DOB cannot be empty.");
                    return;
                }
                sql = baseSearchQuery() + " WHERE d.DOB = ? GROUP BY "
                    + "e.empid, e.Fname, e.Lname, e.SSN, e.email, e.HireDate, e.Salary, "
                    + "d.DOB, d.phone, d.emergency_contact_name, d.emergency_contact_phone";
            }
            case "4" -> {
                System.out.print("Enter SSN: ");
                searchValue = scanner.nextLine().trim();
                if (searchValue.isEmpty()) {
                    System.out.println("SSN cannot be empty.");
                    return;
                }
                sql = baseSearchQuery() + " WHERE e.SSN = ? GROUP BY "
                    + "e.empid, e.Fname, e.Lname, e.SSN, e.email, e.HireDate, e.Salary, "
                    + "d.DOB, d.phone, d.emergency_contact_name, d.emergency_contact_phone";
            }
            default -> {
                System.out.println("Invalid search option.");
                return;
            }
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (useLike) {
                stmt.setString(1, searchValue);
                stmt.setString(2, searchValue);
                stmt.setString(3, searchValue);
            } else if (empId != null) {
                stmt.setInt(1, empId);
            } else {
                stmt.setString(1, searchValue);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    displayEmployeeRecord(rs);
                    displayPayrollHistory(conn, rs.getInt("empid"));
                    System.out.println();
                }
                if (!found) {
                    System.out.println("\nNo employee data found matching your search.");
                }
            }
        } catch (SQLException e) {
            System.out.println("\nError searching employee data: " + e.getMessage());
        }
    }

    private static String baseSearchQuery() {
        return """
            SELECT e.empid,
                   e.Fname,
                   e.Lname,
                   e.SSN,
                   e.email,
                   e.HireDate,
                   e.Salary,
                   d.DOB,
                   d.phone,
                   d.emergency_contact_name,
                   d.emergency_contact_phone,
                   GROUP_CONCAT(DISTINCT divisionTbl.Name SEPARATOR ', ') AS division_names,
                   GROUP_CONCAT(DISTINCT jt.job_title SEPARATOR ', ') AS job_titles
            FROM employees e
            LEFT JOIN demographics d ON d.empID = e.empid
            LEFT JOIN employee_division ed ON ed.empid = e.empid
            LEFT JOIN `division` divisionTbl ON divisionTbl.divID = ed.div_ID
            LEFT JOIN employee_job_titles ejt ON ejt.empid = e.empid
            LEFT JOIN job_titles jt ON jt.job_title_id = ejt.job_title_id
            """;
    }

    private static void displayEmployeeRecord(ResultSet rs) throws SQLException {
        System.out.println("\n--- Employee Record ---");
        System.out.println("Employee ID: " + rs.getString("empid"));
        System.out.println("Name: " + rs.getString("Fname") + " " + rs.getString("Lname"));
        System.out.println("DOB: " + rs.getString("DOB"));
        System.out.println("SSN: " + rs.getString("SSN"));
        System.out.println("Email: " + rs.getString("email"));
        System.out.println("Hire Date: " + rs.getString("HireDate"));
        System.out.println("Salary: " + rs.getString("Salary"));
        System.out.println("Phone: " + rs.getString("phone"));
        System.out.println("Emergency Contact: " + rs.getString("emergency_contact_name"));
        System.out.println("Emergency Contact Phone: " + rs.getString("emergency_contact_phone"));
        System.out.println("Division(s): " + rs.getString("division_names"));
        System.out.println("Job Title(s): " + rs.getString("job_titles"));
    }

    private static void displayPayrollHistory(Connection conn, int empId) throws SQLException {
        String payrollSql = """
                SELECT pay_date,
                       earnings,
                       fed_tax,
                       fed_med,
                       fed_SS,
                       state_tax,
                       retire_401k,
                       health_care
                FROM payroll
                WHERE empid = ?
                ORDER BY pay_date DESC
                """;

        try (PreparedStatement payrollStmt = conn.prepareStatement(payrollSql)) {
            payrollStmt.setInt(1, empId);
            try (ResultSet payrollRs = payrollStmt.executeQuery()) {
                if (!payrollRs.next()) {
                    System.out.println("No payroll records found for this employee.");
                    return;
                }

                System.out.println("\nPayroll History:");
                do {
                    System.out.println("  Pay Date: " + payrollRs.getString("pay_date"));
                    System.out.println("    Earnings: " + payrollRs.getString("earnings"));
                    System.out.println("    Fed Tax: " + payrollRs.getString("fed_tax"));
                    System.out.println("    Fed Med: " + payrollRs.getString("fed_med"));
                    System.out.println("    Fed SS: " + payrollRs.getString("fed_SS"));
                    System.out.println("    State Tax: " + payrollRs.getString("state_tax"));
                    System.out.println("    401k: " + payrollRs.getString("retire_401k"));
                    System.out.println("    Health Care: " + payrollRs.getString("health_care"));
                } while (payrollRs.next());
            }
        }
    }
}