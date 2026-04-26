import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeeEditor {

    private static class EmployeeSummary {
        final int empId;
        final String fullName;

        EmployeeSummary(int empId, String fullName) {
            this.empId = empId;
            this.fullName = fullName;
        }
    }

    private static class EmployeeRecord {
        final int empId;
        String firstName;
        String lastName;
        String email;
        String ssn;
        String hireDate;
        double salary;
        String dob;
        String phone;
        String emergencyContactName;
        String emergencyContactPhone;

        EmployeeRecord(int empId,
                       String firstName,
                       String lastName,
                       String email,
                       String ssn,
                       String hireDate,
                       double salary,
                       String dob,
                       String phone,
                       String emergencyContactName,
                       String emergencyContactPhone) {
            this.empId = empId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.ssn = ssn;
            this.hireDate = hireDate;
            this.salary = salary;
            this.dob = dob;
            this.phone = phone;
            this.emergencyContactName = emergencyContactName;
            this.emergencyContactPhone = emergencyContactPhone;
        }
    }

    public static void editEmployeeData(Scanner scanner) {
        System.out.println("\nEdit Employee Data (HR Admin only)");
        System.out.println("Search by:");
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

        EmployeeRecord employee = fetchEmployee(empId);
        if (employee == null) {
            System.out.println("Employee not found.");
            return;
        }

        while (true) {
            printEmployeeDetails(employee);

            System.out.println("\nEdit options:");
            System.out.println("1. First Name");
            System.out.println("2. Last Name");
            System.out.println("3. Email");
            System.out.println("4. SSN");
            System.out.println("5. Hire Date");
            System.out.println("6. Salary");
            System.out.println("7. DOB");
            System.out.println("8. Phone");
            System.out.println("9. Emergency Contact Name");
            System.out.println("10. Emergency Contact Phone");
            System.out.println("11. Exit editor");
            System.out.print("Select option (1-11): ");

            String editChoice = scanner.nextLine().trim();
            switch (editChoice) {
                case "1" -> updateEmployeeField(scanner, employee, "Fname", "First Name");
                case "2" -> updateEmployeeField(scanner, employee, "Lname", "Last Name");
                case "3" -> updateEmployeeField(scanner, employee, "email", "Email");
                case "4" -> updateEmployeeField(scanner, employee, "SSN", "SSN");
                case "5" -> updateEmployeeField(scanner, employee, "HireDate", "Hire Date");
                case "6" -> applySalaryIncrease(scanner, employee);
                case "7" -> updateDemographicField(scanner, employee, "DOB", "DOB");
                case "8" -> updateDemographicField(scanner, employee, "phone", "Phone");
                case "9" -> updateDemographicField(scanner, employee, "emergency_contact_name", "Emergency Contact Name");
                case "10" -> updateDemographicField(scanner, employee, "emergency_contact_phone", "Emergency Contact Phone");
                case "11" -> {
                    System.out.println("Exiting employee editor.");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static Integer searchByEmployeeId(Scanner scanner) {
        System.out.print("Enter Employee ID: ");
        String idText = scanner.nextLine().trim();
        if (idText.isEmpty()) {
            System.out.println("Employee ID cannot be empty.");
            return null;
        }
        try {
            return Integer.parseInt(idText);
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

    private static EmployeeRecord fetchEmployee(int empId) {
        String sql = """
            SELECT e.empid,
                   e.Fname,
                   e.Lname,
                   e.email,
                   e.SSN,
                   e.HireDate,
                   e.Salary,
                   d.DOB,
                   d.phone,
                   d.emergency_contact_name,
                   d.emergency_contact_phone
            FROM employees e
            LEFT JOIN demographics d ON d.empID = e.empid
            WHERE e.empid = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new EmployeeRecord(
                        rs.getInt("empid"),
                        rs.getString("Fname"),
                        rs.getString("Lname"),
                        rs.getString("email"),
                        rs.getString("SSN"),
                        rs.getString("HireDate"),
                        rs.getDouble("Salary"),
                        rs.getString("DOB"),
                        rs.getString("phone"),
                        rs.getString("emergency_contact_name"),
                        rs.getString("emergency_contact_phone"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error loading employee record: " + e.getMessage());
        }
        return null;
    }

    private static void printEmployeeDetails(EmployeeRecord employee) {
        System.out.println("\n--- Selected Employee ---");
        System.out.println("Employee ID: " + employee.empId);
        System.out.println("Name: " + employee.firstName + " " + employee.lastName);
        System.out.println("Email: " + employee.email);
        System.out.println("SSN: " + employee.ssn);
        System.out.println("Hire Date: " + employee.hireDate);
        System.out.println("Salary: " + employee.salary);
        System.out.println("DOB: " + employee.dob);
        System.out.println("Phone: " + employee.phone);
        System.out.println("Emergency Contact: " + employee.emergencyContactName);
        System.out.println("Emergency Contact Phone: " + employee.emergencyContactPhone);
    }

    private static void applySalaryIncrease(Scanner scanner, EmployeeRecord employee) {
        System.out.printf("Current salary: %.2f%n", employee.salary);
        System.out.print("Enter percentage increase: ");
        String percentText = scanner.nextLine().trim();
        double percent;
        try {
            percent = Double.parseDouble(percentText);
        } catch (NumberFormatException e) {
            System.out.println("Invalid percentage.");
            return;
        }

        if (percent <= 0) {
            System.out.println("Percentage must be greater than zero.");
            return;
        }

        double maxAllowed = maxAllowedIncrease(employee.salary);
        if (percent > maxAllowed) {
            System.out.printf("Salary increase not allowed. Maximum for current salary range is %.1f%%%n", maxAllowed);
            return;
        }

        double newSalary = employee.salary * (1.0 + percent / 100.0);
        String updateSql = "UPDATE employees SET Salary = ? WHERE empid = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {

            stmt.setDouble(1, newSalary);
            stmt.setInt(2, employee.empId);
            if (stmt.executeUpdate() == 1) {
                System.out.printf("Salary updated to %.2f%n", newSalary);
                employee.salary = newSalary;
            } else {
                System.out.println("Salary update failed.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating salary: " + e.getMessage());
        }
    }

    private static double maxAllowedIncrease(double salary) {
        if (salary < 50000) return 10.0;
        if (salary < 80000) return 7.0;
        if (salary < 120000) return 5.0;
        return 3.0;
    }

    private static void updateEmployeeField(Scanner scanner, EmployeeRecord employee, String column, String label) {
        System.out.print("Enter new " + label + ": ");
        String value = scanner.nextLine().trim();
        if (value.isEmpty()) {
            System.out.println(label + " cannot be empty.");
            return;
        }

        String sql = "UPDATE employees SET " + column + " = ? WHERE empid = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if ("Salary".equals(column)) {
                stmt.setDouble(1, Double.parseDouble(value));
            } else {
                stmt.setString(1, value);
            }
            stmt.setInt(2, employee.empId);

            if (stmt.executeUpdate() == 1) {
                switch (column) {
                    case "Fname" -> employee.firstName = value;
                    case "Lname" -> employee.lastName = value;
                    case "email" -> employee.email = value;
                    case "SSN" -> employee.ssn = value;
                    case "HireDate" -> employee.hireDate = value;
                    case "Salary" -> employee.salary = Double.parseDouble(value);
                }
                System.out.println(label + " updated successfully.");
            } else {
                System.out.println("Update failed.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating " + label + ": " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format for " + label + ".");
        }
    }

    private static void updateDemographicField(Scanner scanner, EmployeeRecord employee, String column, String label) {
        System.out.print("Enter new " + label + ": ");
        String value = scanner.nextLine().trim();
        if (value.isEmpty()) {
            System.out.println(label + " cannot be empty.");
            return;
        }

        String sql = """
            INSERT INTO demographics (empID, DOB, phone, emergency_contact_name, emergency_contact_phone)
            VALUES (?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
              DOB = VALUES(DOB),
              phone = VALUES(phone),
              emergency_contact_name = VALUES(emergency_contact_name),
              emergency_contact_phone = VALUES(emergency_contact_phone)
            """;

        String dob = employee.dob;
        String phone = employee.phone;
        String emergencyName = employee.emergencyContactName;
        String emergencyPhone = employee.emergencyContactPhone;

        switch (column) {
            case "DOB" -> dob = value;
            case "phone" -> phone = value;
            case "emergency_contact_name" -> emergencyName = value;
            case "emergency_contact_phone" -> emergencyPhone = value;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employee.empId);
            stmt.setString(2, dob);
            stmt.setString(3, phone);
            stmt.setString(4, emergencyName);
            stmt.setString(5, emergencyPhone);

            if (stmt.executeUpdate() >= 1) {
                employee.dob = dob;
                employee.phone = phone;
                employee.emergencyContactName = emergencyName;
                employee.emergencyContactPhone = emergencyPhone;
                System.out.println(label + " updated successfully.");
            } else {
                System.out.println("Update failed.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating " + label + ": " + e.getMessage());
        }
    }
}