import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class JdbcConnectionControlExample {

    private static void cleanUpResources(Connection connection, Statement statement, ResultSet resultSet, Scanner scanner) throws SQLException {
        if (connection != null)
            connection.close();
        if (statement != null)
            statement.close();
        if (resultSet != null)
            resultSet.close();
        if (scanner != null)
            scanner.close();
        log.info("DB 연결 종료 및 자원 해제");
    }
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            while (true) {
                System.out.println("입력하세요.: ");
                System.out.println("c - DB 연결");
                System.out.println("r - Query 실행");
                System.out.println("q - DB 연결 종료");
                System.out.println("종료하려면 'exit' 입력");

                String input = scanner.nextLine();

                if (input.equalsIgnoreCase("c")) {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    if (connection == null || connection.isClosed()) {
                        connection = DriverManager.getConnection(
                                "jdbc:mysql://localhost:3306/rdb_study", "root", ""
                        );
                        log.info("DB 연결 성공");
                    } else {
                        log.warn("이미 DB 연결 되어 있음");
                    }
                } else if (input.equalsIgnoreCase("r")) {
                    final String query = "SELECT id, name, position, salary FROM employee";
                    pstmt = connection.prepareStatement(query);
                    rs = pstmt.executeQuery();
                    System.out.println();
                    while (rs.next()) {
                        int id = rs.getInt(1);
                        String name = rs.getString(2);
                        String position = rs.getString(3);
                        int salary = rs.getInt(4);

                        EmployeeResponse employee = new EmployeeResponse(id, name, position, salary);

                        log.info("employee: {}", employee);
                    }
                } else if (input.equalsIgnoreCase("q")) {
                    assert connection != null;
                    log.info("DB 연결 종료");
                    connection.close();
                } else if (input.equalsIgnoreCase("exit")) {
                    log.info("프로그램 종료");
                    cleanUpResources(connection, pstmt, rs, scanner);
                    break;
                }
            }
        } finally {
            cleanUpResources(connection, pstmt, rs, scanner);
        }
    }
}
