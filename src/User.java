public class User {
    private final int empID;
    private final String username;
    private String role;
    private employee emp;

    public User(int empID, String username, String role, employee emp) {
        this.empID = empID;
        this.username = username;
        this.role = role;
        this.emp = emp;
    }

    // Getters
    public int getEmpID() {return empID;}
    public String getUsername() {return username;}
    public String getRole() {return role;}
    public employee getEmployee() {return emp;}
    
    // Convenience getters that delegate to employee object
    public String getFname() {return emp != null ? emp.getFname() : "";}
    public String getLname() {return emp != null ? emp.getLname() : "";}
    public String getEmail() {return emp != null ? emp.getEmail() : "";}

    //Setters
    public void setRole(String role) {this.role = role;}
    public void setEmployee(employee emp) {this.emp = emp;}

    // Role-based permission checks
    public boolean isHRAdmin() {
        return "HR_ADMIN".equals(role);
    }

    public boolean isGeneralEmployee() {
        return "GENERAL_EMPLOYEE".equals(role);
    }
}
