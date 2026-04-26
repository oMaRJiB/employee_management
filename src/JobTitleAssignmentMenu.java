import java.sql.*;
import java.util.*;

public class JobTitleAssignmentMenu {
    private final Scanner scanner;

    private static class EmployeeSummary {
        final int empId;
        final String fullName;

        EmployeeSummary(int empId, String fullName) {
            this.empId = empId;
            this.fullName = fullName;
        }
    }

    public JobTitleAssignmentMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    private static Integer searchByEmployeeId(Scanner scanner) {
        System.out.print("Enter Employee ID: ");
        String idText = scanner.nextLine().trim();
        if (idText.isEmpty()) {
            System.out.println("Employee ID cannot be empty.");
            return null;
        }
        try {
            return Integer.valueOf(idText);
        } catch (NumberFormatException e) {
            System.out.println("Employee ID must be a number.");
            return null;
        }
    }

    private static Integer searchByName(Scanner scanner) {
        System.out.print("Enter full or partial name: ");
        String searchValue = scanner.nextLine().trim();
        if (searchValue.isEmpty()) {
            System.out.println("Name search text cannot be empty.");
            return null;
        }
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
        return chooseEmployeeFromSearch(scanner, searchValue, sql, true);
    }

    private static Integer searchByDob(Scanner scanner) {
        System.out.print("Enter DOB (YYYY-MM-DD): ");
        String searchValue = scanner.nextLine().trim();
        if (searchValue.isEmpty()) {
            System.out.println("DOB cannot be empty.");
            return null;
        }
        String sql = """
            SELECT e.empid,
                   e.Fname,
                   e.Lname
            FROM employees e
            LEFT JOIN demographics d ON d.empID = e.empid
            WHERE d.DOB = ?
            ORDER BY e.empid
            """;
        return chooseEmployeeFromSearch(scanner, searchValue, sql, false);
    }

    private static Integer searchBySsn(Scanner scanner) {
        System.out.print("Enter SSN: ");
        String searchValue = scanner.nextLine().trim();
        if (searchValue.isEmpty()) {
            System.out.println("SSN cannot be empty.");
            return null;
        }
        String sql = """
            SELECT e.empid,
                   e.Fname,
                   e.Lname
            FROM employees e
            WHERE e.SSN = ?
            ORDER BY e.empid
            """;
        return chooseEmployeeFromSearch(scanner, searchValue, sql, false);
    }

    private static Integer chooseEmployeeFromSearch(Scanner scanner, String searchValue, String sql, boolean useLike) {
        List<EmployeeSummary> matches = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (useLike) {
                String likeValue = "%" + searchValue.toLowerCase() + "%";
                stmt.setString(1, likeValue);
                stmt.setString(2, likeValue);
                stmt.setString(3, likeValue);
            } else {
                stmt.setString(1, searchValue);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    matches.add(new EmployeeSummary(
                        rs.getInt("empid"),
                        rs.getString("Fname") + " " + rs.getString("Lname")));
                }
            }
        } catch (SQLException e) {
            System.out.println("\nError searching employees: " + e.getMessage());
            return null;
        }

        if (matches.isEmpty()) {
            System.out.println("\nNo employee data found matching your search.");
            return null;
        }

        if (matches.size() == 1) {
            EmployeeSummary single = matches.get(0);
            System.out.printf("\nEmployee found: %d - %s%n", single.empId, single.fullName);
            System.out.print("Is this the employee you want to assign to? (yes/no): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            if (confirm.equals("yes") || confirm.equals("y")) {
                return single.empId;
            }
            System.out.println("Search cancelled.");
            return null;
        }

        System.out.println();
        System.out.printf("%-12s | %s%n", "Employee ID", "Name");
        System.out.println("-------------+---------------------------");
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

    public void showJobTitleAssignmentMenu() {
        System.out.println("\nAssign Job Title");
        System.out.println("Search for Employee:");
        System.out.println("1. Employee ID");
        System.out.println("2. Name");
        System.out.println("3. Date of Birth (YYYY-MM-DD)");
        System.out.println("4. SSN");
        System.out.print("Select option (1-4): ");

        String option = scanner.nextLine().trim();
        Integer empId = null;

        switch (option) {
            case "1" -> empId = searchByEmployeeId(scanner);
            case "2" -> empId = searchByName(scanner);
            case "3" -> empId = searchByDob(scanner);
            case "4" -> empId = searchBySsn(scanner);
            default -> {
                System.out.println("Invalid search option.");
                return;
            }
        }

        if (empId == null) {
            return;
        }

        System.out.print("Enter job title to search (or press Enter to list all): ");
        String searchTerm = scanner.nextLine().trim();
        List<Map<String, Object>> jobTitles;
        if (searchTerm.isEmpty()) {
            jobTitles = JobTitleDA.getJobTitles();
        } else {
            jobTitles = JobTitleDA.searchJobTitles(searchTerm);
        }

        if (jobTitles.isEmpty()) {
            System.out.println("No job titles found.");
            return;
        }

        System.out.println("\nAvailable Job Titles:");
        for (Map<String, Object> jt : jobTitles) {
            System.out.println(jt.get("job_title_id") + ". " + jt.get("job_title"));
        }

        System.out.print("Enter Job Title ID to assign: ");
        String jtIdStr = scanner.nextLine().trim();
        try {
            int jobTitleID = Integer.parseInt(jtIdStr);
            if (JobTitleDA.assignJobTitle(empId, jobTitleID)) {
                System.out.println("Job title assigned successfully.");
            } else {
                System.out.println("Failed to assign job title.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid Job Title ID. Must be a number.");
        }
    }
}
