import java.sql.*;
import java.util.Scanner;

public class AdminFactory {
    private final Scanner scanner;
    private final String dbUrl;
    private final String dbName;
    private final String dbPass;

    public AdminFactory(Scanner scanner, String dbUrl, String dbName, String dbPass) {
        this.scanner = scanner;
        this.dbUrl = dbUrl;
        this.dbName = dbName;
        this.dbPass = dbPass;
    }

    public void registerUser() {
        System.out.print("Въведете потребителско име: ");
        String userName = scanner.nextLine();
        System.out.print("Въведете парола: ");
        String password = scanner.nextLine();

        UserFactory userFactory = new UserFactory();
        if (userFactory.checkUserIfExist(userName, password, dbUrl, dbName, dbPass) != null) {
            System.out.println("Потребител с такова име вече е регистриран!");
            return;
        }


        System.out.println("Въведете позиция(админ/служител)");
        String userType = scanner.nextLine();
        if (userType.equalsIgnoreCase("админ")) {
            String insertUserSql = "insert into users(name,role,password) values(?,?,?)";
            String insertAdminSql = "insert into admins(user_id, user_name)values (?, ?)";

            try (Connection connection = DriverManager.getConnection(dbUrl, dbName, dbPass);
                 PreparedStatement preparedStatementUser = connection.prepareStatement(insertUserSql, PreparedStatement.RETURN_GENERATED_KEYS);
                 PreparedStatement preparedStatementAdmin = connection.prepareStatement(insertAdminSql)) {
                preparedStatementUser.setString(1, userName);
                preparedStatementUser.setString(2, "ADMIN");
                preparedStatementUser.setString(3, password);

                int affectedRows = preparedStatementUser.executeUpdate();

                if (affectedRows > 0) {
                    ResultSet generatedKeys = preparedStatementUser.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);

                        preparedStatementAdmin.setInt(1, userId);
                        preparedStatementAdmin.setString(2, userName);

                        preparedStatementAdmin.executeUpdate();
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("Успешна регистрация на администратор!");


        } else if (userType.equalsIgnoreCase("служител")) {
            System.out.print("Въведете фамилия на служителя: ");
            String lastName = scanner.nextLine();

            String insertUserSql = "insert into users(name,role,password) values(?,?,?)";
            String insertEmployeeSql = "insert into employees(user_id, first_name, last_name)values (?, ?, ?)";
            String insertEmployeeStatsSql = "insert into employee_registrations(employee_id) values (?)";

            try (Connection conn = DriverManager.getConnection(dbUrl, dbName, dbPass);
                 PreparedStatement preparedStatementUser = conn.prepareStatement(insertUserSql, PreparedStatement.RETURN_GENERATED_KEYS);
                 PreparedStatement preparedStatementEmployee = conn.prepareStatement(insertEmployeeSql, PreparedStatement.RETURN_GENERATED_KEYS);
                 PreparedStatement preparedStatementStats = conn.prepareStatement(insertEmployeeStatsSql)) {
                preparedStatementUser.setString(1, userName);
                preparedStatementUser.setString(2, "EMPLOYEE");
                preparedStatementUser.setString(3, password);

                int affectedRows = preparedStatementUser.executeUpdate();

                if (affectedRows > 0) {
                    ResultSet generatedKeys = preparedStatementUser.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);

                        preparedStatementEmployee.setInt(1, userId);
                        preparedStatementEmployee.setString(2, userName);
                        preparedStatementEmployee.setString(3, lastName);

                        affectedRows = preparedStatementEmployee.executeUpdate();
                    }
                }
                if (affectedRows > 0) {
                    ResultSet generatedKeys = preparedStatementEmployee.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int employeeId = generatedKeys.getInt(1);

                        preparedStatementStats.setInt(1, employeeId);

                        preparedStatementStats.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("Успешна регистрация на служител!");

        } else System.out.println("Няма такава позиция!");

    }

    public void removeUser() {
        System.out.println("Въведете информация за потребителя, който да бъде изтрит.");
        System.out.print("Въведете потребителско име: ");
        String userName = scanner.nextLine();
        System.out.print("Въведете парола: ");
        String password = scanner.nextLine();

        UserFactory userFactory = new UserFactory();
        UserType userType = userFactory.checkUserIfExist(userName, password, dbUrl, dbName, dbPass);
        if (userType == null) {
            System.out.println("Няма намерен потребител!");
            return;
        }
        String deleteSql = "delete from users where name = ? and password = ?";

        try (Connection connection = DriverManager.getConnection(dbUrl, dbName, dbPass);
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSql)) {

            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, password);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Успешна премахване на потребител!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void checkEmployeeStats() {
        String registrationCountSql = "SELECT e.first_name, e.last_name,er.employee_id AS id, er.registration_count AS count\n" +
                "FROM employees e\n" +
                "JOIN employee_registrations er ON e.employee_id = er.employee_id;";

        try (Connection connection = DriverManager.getConnection(dbUrl, dbName, dbPass);
             PreparedStatement preparedStatement = connection.prepareStatement(registrationCountSql);
             ResultSet rs = preparedStatement.executeQuery()) {

            System.out.println("ID|Име    |    Брой");
            while (rs.next()) {

                int id = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                int count = rs.getInt("count");
                System.out.println(id + "|" + firstName+ " " + lastName+ " - " + count);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.print("Искате ли да се покажат алктуалните регистрираните потребители за някой служител: ");
        String answer = scanner.nextLine();
        if (answer.equalsIgnoreCase("не")){
            return;
        }
        System.out.print("Въведете ID на служителя: ");
        int id = Integer.parseInt(scanner.nextLine());


        String showDetailsForCustomerSql = "select c.first_name, c.last_name, c.number, r.registration_date from customers c join employee_customer_registrations r on c.customer_id = r.customer_id where r.employee_id = ?";
        try (Connection connection = DriverManager.getConnection(dbUrl,dbName,dbPass);
        PreparedStatement preparedStatement = connection.prepareStatement(showDetailsForCustomerSql)){
            preparedStatement.setInt(1,id);
            ResultSet rs = preparedStatement.executeQuery();
            System.out.println("Име и фамилия на клиент  |номер | дата на регистрация");
            while (rs.next()){
                String fName = rs.getString("c.first_name");
                String lName = rs.getString("c.last_name");
                String number = rs.getString("c.number");
                Date date = rs.getDate("r.registration_date");
                System.out.println(fName + " "+ lName + " "+ number+ " "+ date);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void resetEmployeeStats() {

    }
}

