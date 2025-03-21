import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JdbcExample {
    public static void main(String[] args) throws SQLException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<EmployeeResponse> employees = new ArrayList<>();
        log.info("hello");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/rdb_study", "root", ""
            );
            final String SQL = "SELECT id, name, position, salary FROM employee";
            pstmt = connection.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            System.out.println();
            while (rs.next()) {
                 int id = rs.getInt(1);
                 String name = rs.getString(2);
                 String position = rs.getString(3);
                 int salary = rs.getInt(4);
                 employees.add(new EmployeeResponse(id, name, position, salary));
//                 System.out.print("id = " + id + ", ");
//                 System.out.print("name = " + name + ", ");
//                 System.out.print("position = " + position + ", ");
//                 System.out.println("salary = " + salary);
             }
        } catch (Exception e) {
            System.out.println("에러");
            e.printStackTrace();
            // throw new RuntimeException(e);
        } finally {
            // Leak 누수 CPU, RAM, DB Pool
            if (connection != null) {
                connection.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        for (EmployeeResponse employee : employees) {
            System.out.println(employee);
        }
    }
}
