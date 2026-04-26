import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class JobTitleAssignmentMenu {
    private final Scanner scanner;

    public JobTitleAssignmentMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showJobTitleAssignmentMenu() {
        System.out.println("\nAssign Job Title");
        System.out.print("Enter Employee ID: ");
        String empIdStr = scanner.nextLine().trim();
        try {
            int empID = Integer.parseInt(empIdStr);
            // Check if employee exists
            if (employeeDA.getEmployeeByID(empID) == null) {
                System.out.println("Employee not found.");
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
                if (JobTitleDA.assignJobTitle(empID, jobTitleID)) {
                    System.out.println("Job title assigned successfully.");
                } else {
                    System.out.println("Failed to assign job title.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid Job Title ID. Must be a number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid Employee ID. Must be a number.");
        }
    }
}
