import java.io.Console;
import java.util.Scanner;

public class LoginManager {
    private static final int MAX_ATTEMPTS = 999;
    private User currentUser;
    private final Scanner scanner;

    public LoginManager(Scanner scanner) {
        this.scanner = scanner;
        this.currentUser = null;
    }
    public User login() {
        int attempts = 0;
        Console console = System.console();

        while (attempts < MAX_ATTEMPTS) {
            System.out.println("\n========================================");
            System.out.println("   EMPLOYEE MANAGEMENT SYSTEM LOGIN");
            System.out.println("========================================");
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();

            char[] passwordChars = console.readPassword("Enter password: ");
            String password = new String(passwordChars);

            User user = employeeDA.authenticate(username, password);

            if (user != null) {
                this.currentUser = user;
                System.out.println("\n✓ Login successful!");
                System.out.println("Welcome, " + user.getFname() + " " + user.getLname());
                System.out.println("Role: " + user.getRole());
                return user;
            } else {
                attempts++;
                System.out.println("\n✗ Login failed: Invalid username or password.");
                if (attempts < MAX_ATTEMPTS) {
                    System.out.println("Attempts remaining: " + (MAX_ATTEMPTS - attempts));
                }
            }
        }

        System.out.println("\n✗ Maximum login attempts exceeded. Access denied.");
        return null;
    }
    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        if (currentUser != null) {
            System.out.println("\nLogging out " + currentUser.getUsername() + "...");
            currentUser = null;
            System.out.println("You have been logged out successfully.");
        }
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
