import java.sql.*;
import java.util.*;

public class JobTitleDA {

    public static List<Map<String, Object>> getJobTitles() {
        List<Map<String, Object>> jobTitles = new ArrayList<>();
        String query = "SELECT job_title_id, job_title FROM job_titles ORDER BY job_title";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> jt = new HashMap<>();
                jt.put("job_title_id", rs.getInt("job_title_id"));
                jt.put("job_title", rs.getString("job_title"));
                jobTitles.add(jt);
            }
        } catch (SQLException e) {
            System.err.println("Database error retrieving job titles: " + e.getMessage());
        }
        return jobTitles;
    }

    public static List<Map<String, Object>> searchJobTitles(String title) {
        List<Map<String, Object>> jobTitles = new ArrayList<>();
        String query = "SELECT job_title_id, job_title FROM job_titles WHERE job_title LIKE ? ORDER BY job_title";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, "%" + title + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> jt = new HashMap<>();
                jt.put("job_title_id", rs.getInt("job_title_id"));
                jt.put("job_title", rs.getString("job_title"));
                jobTitles.add(jt);
            }
        } catch (SQLException e) {
            System.err.println("Database error searching job titles: " + e.getMessage());
        }
        return jobTitles;
    }

    public static boolean assignJobTitle(int empID, int jobTitleID) {
        // Check if already assigned
        String checkQuery = "SELECT COUNT(*) FROM employee_job_titles WHERE empid = ? AND job_title_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, empID);
            checkStmt.setInt(2, jobTitleID);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Employee already has this job title.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error checking assignment: " + e.getMessage());
            return false;
        }

        String insertQuery = "INSERT INTO employee_job_titles (empid, job_title_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setInt(1, empID);
            pstmt.setInt(2, jobTitleID);
            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Database error assigning job title: " + e.getMessage());
            return false;
        }
    }
}
