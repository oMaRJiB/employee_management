import java.sql.*;
import java.util.Scanner;

public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static final LoginManager loginManager = new LoginManager(scanner);
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
        while (run) {
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

        if (user.isHRAdmin()) {
            return showHRAdminMenu();
        } else if (user.isGeneralEmployee()) {
            return showGeneralEmployeeMenu();
        }
        return false;
    }

    private static boolean showHRAdminMenu() {
        System.out.println("\nHR Admin Menu:");
        System.out.println("1. Search Employee Data (View)");
        System.out.println("2. Edit Employee Data");
        System.out.println("3. Update Employee Salary");
        System.out.println("4. View Reports");
        System.out.println("5. Logout");
        System.out.print("\nSelect an option (1-5): ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> System.out.println("\n[FEATURE NOT YET IMPLEMENTED: Search Employee Data]");
            case "2" -> System.out.println("\n[FEATURE NOT YET IMPLEMENTED: Edit Employee Data]");
            case "3" -> System.out.println("\n[FEATURE NOT YET IMPLEMENTED: Update Salary]");
            case "4" -> System.out.println("\n[FEATURE NOT YET IMPLEMENTED: View Reports]");
            case "5" -> {loginManager.logout();return true;}
            default -> System.out.println("\n✗ Invalid option. Please try again.");
        }

        return true;
    }

    private static boolean showGeneralEmployeeMenu() {
        System.out.println("\nGeneral Employee Menu:");
        System.out.println("1. View My Demographic Information");
        System.out.println("2. View My Pay Statement History");
        System.out.println("3. Logout");
        System.out.print("\nSelect an option (1-3): ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> System.out.println("\n[FEATURE NOT YET IMPLEMENTED: View Demographic Information]");
            case "2" -> System.out.println("\n[FEATURE NOT YET IMPLEMENTED: View Pay Statement History]");
            case "3" -> {
                loginManager.logout();
                return true;
            }
            default -> System.out.println("\n✗ Invalid option. Please try again.");
        }

        return true;
    }
}
