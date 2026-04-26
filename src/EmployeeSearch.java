import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeeSearch {

    private static class EmployeeSummary {
        final int empId;
        final String fullName;

        EmployeeSummary(int empId, String fullName) {
            this.empId = empId;
            this.fullName = fullName;
        }
    }

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
                    empId = Integer.valueOf(searchValue);
                } catch (NumberFormatException e) {
                    System.out.println("Employee ID must be a number.");
                    return;
                }
            }
            case "2" -> {
                System.out.print("Enter full or partial name: ");
                searchValue = scanner.nextLine().trim();
                if (searchValue.isEmpty()) {
                    System.out.println("Name search text cannot be empty.");
                    return;
                }
                empId = chooseEmployeeByName(scanner, searchValue);
                if (empId == null) {
                    return;
                }
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
                runRecordQuery(searchValue, sql, false);
                return;
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
                runRecordQuery(searchValue, sql, false);
                return;
            }
            default -> {
                System.out.println("Invalid search option.");
                return;
            }
        }

        if (empId != null) {
            sql = baseSearchQuery() + " WHERE e.empid = ? GROUP BY "
                + "e.empid, e.Fname, e.Lname, e.SSN, e.email, e.HireDate, e.Salary, "
                + "d.DOB, d.phone, d.emergency_contact_name, d.emergency_contact_phone";
            runRecordQuery(String.valueOf(empId), sql, true);
        }
    }

        private static Integer chooseEmployeeByName(Scanner scanner, String searchValue) {
        String sql = """
            SELECT e.empid,
                   e.Fname,
                   e.Lname
            FROM employees e
            WHERE LOWER(e.Fname) LIKE ?
               OR LOWER(e.Lname) LIKE ?
               OR LOWER(CONCAT(e.Fname, ' ', e.Lname)) LIKE ?
            ORDER BY e.empid
            """;

        List<EmployeeSummary> matches = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String likeValue = "%" + searchValue.toLowerCase() + "%";
            stmt.setString(1, likeValue);
            stmt.setString(2, likeValue);
            stmt.setString(3, likeValue);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    matches.add(new EmployeeSummary(
                        rs.getInt("empid"),
                        rs.getString("Fname") + " " + rs.getString("Lname")));
                }
            }
        } catch (SQLException e) {
            System.out.println("\nError searching employee names: " + e.getMessage());
            return null;
        }

        if (matches.isEmpty()) {
            System.out.println("\nNo employee data found matching your search.");
            return null;
        }

        if (matches.size() == 1) {
            EmployeeSummary single = matches.get(0);
            System.out.printf("\nEmployee found: %d - %s%n", single.empId, single.fullName);
            System.out.print("Is this the employee you want to view? (yes/no): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            if (confirm.equals("yes") || confirm.equals("y")) {
                return single.empId;
            }
            System.out.println("Search cancelled.");
            return null;
        }

        System.out.println();
        System.out.printf("%-12s | %s%n", "Employee ID", "Name");
        System.out.println("-------------+---------------------");
        for (EmployeeSummary item : matches) {
            System.out.printf("%-12d | %s%n", item.empId, item.fullName);
        }

        System.out.print("Enter Employee ID: ");
        String selectedIdText = scanner.nextLine().trim();
        int selectedId;
        try {
            selectedId = Integer.parseInt(selectedIdText);
        } catch (NumberFormatException e) {
            System.out.println("Invalid employee ID.");
            return null;
        }

        boolean idExists = matches.stream().anyMatch(match -> match.empId == selectedId);
        if (!idExists) {
            System.out.println("Employee ID not found in the search results.");
            return null;
        }

        return selectedId;
    }

    private static void runRecordQuery(String parameter, String sql, boolean isNumericId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (isNumericId) {
                stmt.setInt(1, Integer.parseInt(parameter));
            } else {
                stmt.setString(1, parameter);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    displayEmployeeRecord(rs);
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
}