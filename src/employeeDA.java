import java.sql.*;

public class employeeDA {
    public static User authenticate(String username, String password) {
        String query = "SELECT l.empid, l.role, l.password_hash, e.fname, e.lname, e.email, e.hiredate, e.salary, e.ssn " +
                       "FROM login l " +
                       "JOIN employees e ON l.empid = e.empid " +
                       "WHERE l.username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedEncryptedPassword = rs.getString("password_hash");
                
                // Decrypt stored password and compare with provided password
                String decryptedPassword = PasswordEncryption.decrypt(storedEncryptedPassword);
                
                if (decryptedPassword != null && decryptedPassword.equals(password)) {
                    int empID = rs.getInt("empid");
                    String fname = rs.getString("fname");
                    String lname = rs.getString("lname");
                    String email = rs.getString("email");
                    String role = rs.getString("role");
                    java.time.LocalDate hireDate = rs.getDate("hiredate").toLocalDate();
                    double salary = rs.getDouble("salary");
                    String ssn = rs.getString("ssn");
                    
                    // Create employee object
                    employee emp = new employee(empID, fname, lname, email, hireDate, salary, ssn);
                    
                    // Create and return User with employee object
                    return new User(empID, username, role, emp);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during authentication: " + e.getMessage());
        }
        
        return null;
    }

    public static employee getEmployeeByID(int empID) {
        String query = "SELECT empid, fname, lname, email, hiredate, salary, ssn FROM employees WHERE empid = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, empID);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new employee(
                    rs.getInt("empid"),
                    rs.getString("fname"),
                    rs.getString("lname"),
                    rs.getString("email"),
                    rs.getDate("hiredate").toLocalDate(),
                    rs.getDouble("salary"),
                    rs.getString("ssn")
                );
            }
        } catch (SQLException e) {
            System.err.println("Database error retrieving employee: " + e.getMessage());
        }
        
        return null;
    }

    public static employee getEmployeeByUsername(String username) {
        String query = "SELECT e.empid, e.fname, e.lname, e.email, e.hiredate, e.salary, e.ssn " +
                       "FROM employees e " +
                       "JOIN login l ON e.empid = l.empid " +
                       "WHERE l.username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new employee(
                    rs.getInt("empid"),
                    rs.getString("fname"),
                    rs.getString("lname"),
                    rs.getString("email"),
                    rs.getDate("hiredate").toLocalDate(),
                    rs.getDouble("salary"),
                    rs.getString("ssn")
                );
            }
        } catch (SQLException e) {
            System.err.println("Database error retrieving employee: " + e.getMessage());
        }
        
        return null;
    }
    public static boolean createLoginRecord(int empID, String username, String password, String role) {
        String encryptedPassword = PasswordEncryption.encrypt(password);
        
        if (encryptedPassword == null) {
            System.err.println("Failed to encrypt password");
            return false;
        }
        
        String query = "INSERT INTO login (empid, username, password_hash, role) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, empID);
            pstmt.setString(2, username);
            pstmt.setString(3, encryptedPassword);
            pstmt.setString(4, role);
            
            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;
            
        } catch (SQLException e) {
            System.err.println("Database error creating login record: " + e.getMessage());
            return false;
        }
    }
}
