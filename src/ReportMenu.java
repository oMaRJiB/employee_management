import java.util.Scanner;

public class ReportMenu {

    private final Scanner scanner;

    public ReportMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showReportMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n========================================");
            System.out.println("   ADMIN REPORT MENU");
            System.out.println("========================================");
            System.out.println("1. Total Pay by Job Title");
            System.out.println("2. Total Pay by Division");
            System.out.println("3. New Employee Hires by Date Range");
            System.out.println("4. Return to Main Menu");
            System.out.print("\nSelect report type (1-4): ");

            String choice = scanner.nextLine().trim();

            switch(choice) {
                case "1" -> runTotalPayByJobTitleReport();
                case "2" -> runTotalPayByDivisionReport();
                case "3" -> runNewHiresReport();
                case "4" -> running = false;
                default -> System.out.println("\nInvalid option. Please try again.");
            }
        }
    }

    private void runTotalPayByJobTitleReport() {
        int month = getIntInput("Enter month number (1-12): ");
        int year = getIntInput("Enter year (example: 2026): ");

        ReportManager.totalPayByJobTitle(month, year);
    }

    private void runTotalPayByDivisionReport() {
        int month = getIntInput("Enter month number (1-12): ");
        int year = getIntInput("Enter 4-digit year: ");

        ReportManager.totalPayByDivision(month, year);
    }

    private void runNewHiresReport() {
        System.out.print("Enter start date (YYYY-MM-DD): ");
        String startDate = scanner.nextLine().trim();

        System.out.print("Enter end date (YYYY-MM-DD): ");
        String endDate = scanner.nextLine().trim();

        ReportManager.newHires(startDate, endDate);
    }

    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }
}