import java.sql.*;
public class App {
    public static void main(String[] args){

        try(Connection conn = DatabaseConnection.getConnection()){
            System.out.println("Connected!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
