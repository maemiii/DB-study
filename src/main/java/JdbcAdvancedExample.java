import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
public class JdbcAdvancedExample {
    public static void main(String[] args) {
        Connection connection = null;

        try {
            connection = getConnection();
            System.out.println(connection);

            log.debug("직원 전체 조회1 ");
            List<EmployeeResponse> employees = getEmployees(connection,"id", "name", "position", "salary");
            for (EmployeeResponse employee : employees) {
                log.info("result: {}", employee);
            }

            log.debug("직원 단건 조회1");
            EmployeeResponse findEmployee = getEmployeeById1(connection, 1L);
            if (findEmployee != null) {
                // System.out.println(findEmployee);
                log.info("result: {}", findEmployee);
            }

//            log.debug("직원 단건 조회2");
//            // EmployeeResponse findEmployee2 = getEmployeeById2(connection, 99L);
//            // log.info("result: {}", findEmployee2);
//            try {
//                getEmployeeById2(connection, 99L);
//            } catch (NoSuchElementException e) {
//                log.warn("예외 발생 넘기기");
//            }
//
//            log.debug("직원 단건 조회3");
//            Optional<EmployeeResponse> findEmployee3 = getEmployeeById3(connection, 5L);
//            findEmployee3.ifPresent(response -> log.info("result: {}", response));
//
//            log.debug("직원 단건 조회4");
//            Optional<EmployeeResponse> findEmployee4 = getEmployeeById3(connection, 99L);
//            findEmployee4.ifPresentOrElse(response -> log.info("result: {}", response), () -> log.warn("데이터 없음"));

            log.debug("직원 추가1");
            // insertEmployee(connection, "Kim", 25, "Frontend Engineer", 40000, 99);

            log.debug("직원 추가2");
            insertEmployee2(connection, new EmployeeCreateRequest("Lee", 30, "Backend Engineer", 50000, Department.IT));
            log.debug("데이터 삽입 후 직원 전체 조회");
            EmployeeResponse target = null;
            for (EmployeeResponse employee : getEmployees(connection, "id", "name", "position", "salary")) {
                log.info("result: {}", employee);
                target = employee;
            }

            log.debug("직원 수정1");
            updateEmployee1(connection, target.getId(), new EmployeeUpdateRequest("Infra Engineer", 50000));
            log.debug("데이터 수정 후 직원 전체 조회");
            for (EmployeeResponse employee : getEmployees(connection, "id", "name", "position", "salary")) {
                log.info("result: {}", employee);
                target = employee;
            }

            log.debug("직원 수정2");
            // updateEmployee2(connection, target.getId(), new EmployeeUpdateRequest(null, 100_000));
            log.debug("데이터 수정 후 직원 전체 조회");
            for (EmployeeResponse employee : getEmployees(connection, "id", "name", "position", "salary")) {
                log.info("result: {}", employee);
                target = employee;
            }

            log.debug("직원 수정3");
            // updateEmployee3(connection, target.getId(), new EmployeeUpdateRequest(null, 100_000));
            log.debug("데이터 수정 후 직원 전체 조회");
            for (EmployeeResponse employee : getEmployees(connection, "id", "name", "position", "salary")) {
                log.info("result: {}", employee);
                target = employee;
            }

            log.debug("직원 수정4");
            updateEmployee4(connection, target.getId(), new EmployeeUpdateRequest(null, 100_000));
            log.debug("데이터 수정 후 직원 전체 조회");
            for (EmployeeResponse employee : getEmployees(connection, "id", "name", "position", "salary")) {
                log.info("result: {}", employee);
                target = employee;
            }

            log.debug("직원 소프트삭제");
            softDeleteEmployee(connection, target.getId());
            log.debug("데이터 소프트삭제 후 직원 전체 조회");
            System.out.println("size: " + getEmployees(connection, "SELECT id, name, position, salary FROM employee").size());
            for (EmployeeResponse employee : getEmployees(connection, "SELECT id, name, position, salary FROM employee WHERE deleted_at IS NULL")) {
                log.info("result: {}", employee);
            }

            log.debug("직원 하드삭제");
            hardDeleteEmployee(connection, target.getId());
            log.debug("데이터 하드삭제 후 직원 전체 조회");
            for (EmployeeResponse employee : getEmployees(connection, "SELECT id, name, position, salary FROM employee")) {
                log.info("result: {}", employee);
                target = employee;
            }
        } catch (SQLException e) {


        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static EmployeeResponse getEmployeeById1(Connection connection, Long employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement("SELECT id, name, position, salary FROM employee WHERE id = ?");
            pstmt.setLong(1, employeeId);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                String position = rs.getString(3);
                int salary = rs.getInt(4);
                return new EmployeeResponse(id, name, position, salary);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        }
        return null;
    }

    private static EmployeeResponse getEmployeeById2(Connection connection, Long employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement("SELECT id, name, position, salary FROM employee WHERE id = ?");
            pstmt.setLong(1, employeeId);

            rs = pstmt.executeQuery();
            if (!rs.next()) {
                throw new NoSuchElementException("employee not found: " + employeeId);
            }

            int id = rs.getInt(1);
            String name = rs.getString(2);
            String position = rs.getString(3);
            int salary = rs.getInt(4);

            return new EmployeeResponse(id, name, position, salary);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    private static Optional<EmployeeResponse> getEmployeeById3(Connection connection, Long employeeId) throws SQLException {
        final String sql = "SELECT id, name, position, salary FROM employee WHERE id = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement(sql);
            pstmt.setLong(1, employeeId);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                String position = rs.getString(3);
                int salary = rs.getInt(4);
                return Optional.of(new EmployeeResponse(id, name, position, salary));
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        }
        return Optional.empty();
    }


    public static List<EmployeeResponse> getEmployees(Connection connection, String... fields) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<EmployeeResponse> employees = new ArrayList<>();

        try {
            StringBuilder sqlBuilder = new StringBuilder("SELECT ");
            for (String field : fields) {
                sqlBuilder.append(field).append(", ");
            }
            sqlBuilder.delete(sqlBuilder.length() - 2, sqlBuilder.length());
            sqlBuilder.append(" FROM employee");

            String sql = sqlBuilder.toString();
            pstmt = connection.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                String position = rs.getString(3);
                int salary = rs.getInt(4);
                employees.add(new EmployeeResponse(id, name, position, salary));
            }
        } catch (SQLException e) {
            log.error("SQL Error");
            throw e;
        } finally {
            if (pstmt != null)
                pstmt.close();
            if (rs != null)
                rs.close();
        }
        return employees;
    }

    private static void insertEmployee1(Connection connection, String name, int age, String position, int salary, int departmentId) throws SQLException {
        final String sql = "INSERT INTO employee (name, age, position, salary, department_id) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, position);
            pstmt.setInt(4, salary);
            pstmt.setInt(5, departmentId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    private static void insertEmployee2(Connection connection, EmployeeCreateRequest request) throws SQLException {
        final String sql = "INSERT INTO employee (name, age, position, salary, department_id) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, request.getName());
            pstmt.setInt(2, request.getAge());
            pstmt.setString(3, request.getPosition());
            pstmt.setInt(4, request.getSalary());
            pstmt.setInt(5, request.getDepartment().ordinal());

            System.out.println(pstmt);
            // pstmt.execute(); 전체
            // pstmt.executeQuery(); // 조회
            pstmt.executeUpdate(); // 조회 제외(추가, 수정, 삭제)
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    private static void updateEmployee1(Connection connection, @NonNull Long employeeId, EmployeeUpdateRequest request) throws SQLException {
        final String sql = "UPDATE employee SET position = ?, salary = ? WHERE id = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, request.getPosition());
            pstmt.setInt(2, request.getSalary());
            pstmt.setLong(3, employeeId);

            System.out.println(pstmt);
            // pstmt.execute(); 전체
            // pstmt.executeQuery(); // 조회
            pstmt.executeUpdate(); // 조회 제외(추가, 수정, 삭제)
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    private static void updateEmployee2(Connection connection, @NonNull Long employeeId, EmployeeUpdateRequest request) throws SQLException {
        final String sql = "UPDATE employee SET position = ?, salary = ? WHERE id = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement(sql);
            if (request.getPosition() != null) {
                pstmt.setString(1, request.getPosition());
            }
            if (request.getSalary() != null) {
                pstmt.setInt(2, request.getSalary());
            }
            pstmt.setLong(3, employeeId);

            System.out.println(pstmt);
            // pstmt.execute(); 전체
            // pstmt.executeQuery(); // 조회
            pstmt.executeUpdate(); // 조회 제외(추가, 수정, 삭제)
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    private static void updateEmployee3(Connection connection, @NonNull Long employeeId, EmployeeUpdateRequest request) throws SQLException {
        final String sql = String.format("""
            UPDATE employee SET %s, %s WHERE id = ?
            """, request.getPosition() != null ? "position = ?, " : "", request.getSalary() != null ? "salary = ?" : "");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement(sql);
            System.out.println(pstmt);
            if (request.getPosition() != null) {
                pstmt.setString(1, request.getPosition());
            }
            if (request.getSalary() != null) {
                pstmt.setInt(2, request.getSalary());
            }
            pstmt.setLong(3, employeeId);

            // pstmt.execute(); 전체
            // pstmt.executeQuery(); // 조회
            pstmt.executeUpdate(); // 조회 제외(추가, 수정, 삭제)
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    private static void updateEmployee4(Connection connection, @NonNull Long employeeId, EmployeeUpdateRequest request) throws SQLException {
        final String sql = String.format("""
            UPDATE employee SET %s%s WHERE id = ?
            """, request.getPosition() != null ? "position = ?, " : "", request.getSalary() != null ? "salary = ?" : "");
        int parameterCount = 1;
        if (request.getPosition() != null) {
            parameterCount++;
        }
        if (request.getSalary() != null) {
            parameterCount++;
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement(sql);
            System.out.println(pstmt);

            pstmt.setLong(parameterCount--, employeeId);

            if (request.getPosition() != null) {
                pstmt.setString(parameterCount--, request.getPosition());
            }
            if (request.getSalary() != null) {
                pstmt.setInt(parameterCount--, request.getSalary());
            }

            // pstmt.execute(); 전체
            // pstmt.executeQuery(); // 조회
            pstmt.executeUpdate(); // 조회 제외(추가, 수정, 삭제)
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    private static void softDeleteEmployee(Connection connection, Long employeeId) throws SQLException {
        final String sql = "UPDATE employee SET deleted_at = ? WHERE id = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement(sql);
            LocalDateTime now = LocalDateTime.now();
            pstmt.setTimestamp(1, Timestamp.valueOf(now));
            pstmt.setLong(2, employeeId);
            // pstmt.execute(); 전체
            // pstmt.executeQuery(); // 조회
            pstmt.executeUpdate(); // 조회 제외(추가, 수정, 삭제)
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    private static void hardDeleteEmployee(Connection connection, Long employeeId) throws SQLException {
        final String sql = "DELETE FROM employee WHERE id = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement(sql);
            pstmt.setLong(1, employeeId);
            // pstmt.execute(); 전체
            // pstmt.executeQuery(); // 조회
            pstmt.executeUpdate(); // 조회 제외(추가, 수정, 삭제)
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        }
    }


    private static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/rdb_study", "root", ""
        );
    }
}
