import java.sql.*;
import java.util.*;

public class DivisionDA {

    public static List<Map<String, Object>> getDivisions() {
        List<Map<String, Object>> divisions = new ArrayList<>();
        String query = "SELECT divID, Name FROM division ORDER BY Name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> div = new HashMap<>();
                div.put("divID", rs.getInt("divID"));
                div.put("Name", rs.getString("Name"));
                divisions.add(div);
            }
        } catch (SQLException e) {
            System.err.println("Database error retrieving divisions: " + e.getMessage());
        }
        return divisions;
    }

    public static List<Map<String, Object>> searchDivisions(String name) {
        List<Map<String, Object>> divisions = new ArrayList<>();
        String query = "SELECT divID, Name FROM division WHERE Name LIKE ? ORDER BY Name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> div = new HashMap<>();
                div.put("divID", rs.getInt("divID"));
                div.put("Name", rs.getString("Name"));
                divisions.add(div);
            }
        } catch (SQLException e) {
            System.err.println("Database error searching divisions: " + e.getMessage());
        }
        return divisions;
    }

    public static boolean assignDivision(int empID, int divID) {
        // Check if already assigned
        String checkQuery = "SELECT COUNT(*) FROM employee_division WHERE empid = ? AND div_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, empID);
            checkStmt.setInt(2, divID);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Employee is already assigned to this division.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error checking assignment: " + e.getMessage());
            return false;
        }

        String insertQuery = "INSERT INTO employee_division (empid, div_ID) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setInt(1, empID);
            pstmt.setInt(2, divID);
            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Database error assigning division: " + e.getMessage());
            return false;
        }
    }
}
