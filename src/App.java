import java.sql.*;
import java.util.Scanner;

public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static final LoginManager loginManager = new LoginManager(scanner);
    private static final ReportMenu reportMenu = new ReportMenu(scanner);
    private static User currentUser;

    @SuppressWarnings("ConvertToTryWithResources")
    public static void main(String[] args) {
        System.out.println("Employee Management System - Starting...\n");
        // Test database connection
        try (@SuppressWarnings("unused")
        Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Database connection successful!\n");
        } catch (Exception e) {
            System.out.println("Database connection failed: " + e.getMessage());
            System.exit(1);
        }
        // Main application loop
        boolean run = true;
        while(run){
            if (!loginManager.isLoggedIn()) {
                // Show login screen
                currentUser = loginManager.login();
                if (currentUser == null) {
                    // Max login attempts exceeded
                    System.out.println("\nExiting application...");
                    run = false;
                }
            } else {
                // User is logged in - show main menu
                currentUser = loginManager.getCurrentUser();
                if (!showMainMenu(currentUser)) {
                    run = false;
                }
            }
        }
        scanner.close();
        System.out.println("\nThank you for using Employee Management System. Goodbye!");
    }

    private static boolean showMainMenu(User user) {
    System.out.println("\n========================================");
    System.out.println("   MAIN MENU");
    System.out.println("========================================");

    // Common options (ALL users)
    System.out.println("1. Search Employees");             // PT-06 [ALMOST DONE]
    System.out.println("2. View My Pay History");          // PT-07 [DONE]

    // Admin-only options
    if (user.isHRAdmin()) {
        System.out.println("\n--- HR ADMIN FUNCTIONS ---");
        System.out.println("3. Edit Employee");             // PT-01 & PT-05 & PT-03
        System.out.println("4. Delete Employee");           // PT-02 [DONE]
        System.out.println("5. Generate Reports");          // PT-08 [DONE]
        System.out.println("6. Assign Division");           // PT-09
        System.out.println("7. Assign Job Title");          // PT-10
        System.out.println("8. Logout");
    } else {
        System.out.println("\n3. Logout");
    }

    System.out.print("\nSelect an option: ");
    String choice = scanner.nextLine().trim();

    switch (choice) {
        case "1" -> EmployeeSearch.searchEmployeeData(scanner);
        case "2" -> ReportManager.getPayrollHistory(user.getEmpID());
        case "3" -> {
            if (user.isHRAdmin()) {
                System.out.println("\n[PT-05 NOT IMPLEMENTED]");
            } else {
                loginManager.logout();
            }
            }
        case "4" -> {
            if (user.isHRAdmin()) {
                deleteEmployeeMenu();
            }
        }
        case "5" -> {
            if (user.isHRAdmin())
                System.out.println("\n[PT-02 NOT IMPLEMENTED]");
            }
        case "6" -> {
            if (user.isHRAdmin())
                System.out.println("\n[PT-03 NOT IMPLEMENTED]");
            }
        case "7" -> {
            if (user.isHRAdmin())
                reportMenu.showReportMenu();
            }
        case "8" -> {
            if (user.isHRAdmin())
                System.out.println("\n[PT-09 NOT IMPLEMENTED]");
            }
        case "9" -> {
            if (user.isHRAdmin())
                System.out.println("\n[PT-10 NOT IMPLEMENTED]");
            }
        case "10" -> {
            if (user.isHRAdmin()) {
                loginManager.logout();
            }
            }
        default -> System.out.println("\nInvalid option.");
    }
        // Admin-only handling

    return true;
}

    private static void deleteEmployeeMenu() {
        System.out.println("\nDelete Employee");
        System.out.print("Enter Employee ID to delete: ");
        String empIdStr = scanner.nextLine().trim();
        try {
            int empID = Integer.parseInt(empIdStr);
            if (employeeDA.deleteEmployee(empID)) {
                System.out.println("Employee deleted successfully.");
            } else {
                System.out.println("Failed to delete employee. Employee may not exist or has dependent records.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid Employee ID. Must be a number.");
        }
    }
}
