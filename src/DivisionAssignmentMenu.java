import java.sql.*;
import java.util.*;

public class DivisionAssignmentMenu {
    private final Scanner scanner;

    private static class EmployeeSummary {
        final int empId;
        final String fullName;

        EmployeeSummary(int empId, String fullName) {
            this.empId = empId;
            this.fullName = fullName;
        }
    }

    public DivisionAssignmentMenu(Scanner scanner) {
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

    public void showDivisionAssignmentMenu() {
        System.out.println("\nAssign Division");
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

        System.out.print("Enter division name to search (or press Enter to list all): ");
        String searchTerm = scanner.nextLine().trim();
        List<Map<String, Object>> divisions;
        if (searchTerm.isEmpty()) {
            divisions = DivisionDA.getDivisions();
        } else {
            divisions = DivisionDA.searchDivisions(searchTerm);
        }

        if (divisions.isEmpty()) {
            System.out.println("No divisions found.");
            return;
        }

        System.out.println("\nAvailable Divisions:");
        for (Map<String, Object> div : divisions) {
            System.out.println(div.get("divID") + ". " + div.get("Name"));
        }

        System.out.print("Enter Division ID to assign: ");
        String divIdStr = scanner.nextLine().trim();
        try {
            int divID = Integer.parseInt(divIdStr);
            if (DivisionDA.assignDivision(empId, divID)) {
                System.out.println("Division assigned successfully.");
            } else {
                System.out.println("Failed to assign division.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid Division ID. Must be a number.");
        }
    }
}
