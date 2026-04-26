import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class DivisionAssignmentMenu {
    private final Scanner scanner;

    public DivisionAssignmentMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showDivisionAssignmentMenu() {
        System.out.println("\nAssign Division");
        System.out.print("Enter Employee ID: ");
        String empIdStr = scanner.nextLine().trim();
        try {
            int empID = Integer.parseInt(empIdStr);
            // Check if employee exists
            if (employeeDA.getEmployeeByID(empID) == null) {
                System.out.println("Employee not found.");
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
                if (DivisionDA.assignDivision(empID, divID)) {
                    System.out.println("Division assigned successfully.");
                } else {
                    System.out.println("Failed to assign division.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid Division ID. Must be a number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid Employee ID. Must be a number.");
        }
    }
}
