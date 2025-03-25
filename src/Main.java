import java.sql.*;
import java.util.Scanner;


public class Main {
    static final String DB_URL = "jdbc:mysql://localhost/fitness";
    static final String USER = "Koki";
    static final String PASSWORD = "082408";
    private static final Scanner scanner = new Scanner(System.in);
    private static boolean isWorking = true;

    public static void main(String[] args) {

        while (isWorking) {
            start();
        }
    }

    private static void start() {
        if (!isUserTableEmpty()) {
            System.out.println("Здравейте!");
            System.out.println("Въведете име и парола или изход за да излезете:");
            String userName = scanner.nextLine();
            if (userName.equalsIgnoreCase("Изход")) {
                System.out.println("Довиждане");
                isWorking = false;
                return;
            }
            String password = scanner.nextLine();


            UserFactory userFactory = new UserFactory();

            UserType userType = userFactory.checkUserIfExist(userName, password,DB_URL,USER,PASSWORD);

            if (userType!= null) {
                if (userType.equals(UserType.ADMIN)) {
                    AdminMenu.chooseFromAdminMenu(scanner,DB_URL,USER,PASSWORD);
                } else if (userType.equals(UserType.EMPLOYEE)) {
                    int employee_id = userFactory.checkEmployeeId(userName, password,DB_URL,USER,PASSWORD);
                    EmployeeMenu.chooseFromEmployeeMenu(employee_id, scanner,DB_URL,USER,PASSWORD);
                } else {
                    System.out.println("Проблем с позицията!");
                }
            } else {
                System.out.println("Служителя не е намерен!");
                System.out.println("Грешно потребителско име или парола!");
            }

        }
    }

    private static boolean isUserTableEmpty() {
        String sql = "select count(*) AS count from users";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt("count");

                if (count == 0) {
                    System.out.println("Няма регистрирани потребители. Създаване на първи администраторски акаунт...");
                    if (createFirstAdmin()){
                        System.out.println("Администраторският акаунт е създаден успешно!");
                        return false;
                    }
                    else {
                        System.out.println("Грешка при създаване на администраторския акаунт!");
                        return true;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return false;
    }

    private static boolean createFirstAdmin() {
        System.out.print("Въведете потребителско име за администратора: ");
        String userName = scanner.nextLine();

        System.out.print("Въведете парола за администратора: ");
        String password = scanner.nextLine();

        //String passwordHash = SecurityUtil.hashPassword(password);

        String insertUserSql = "insert into users(name,role,password)values(?,?,?)";
        String insertAdminSql = "insert into admins(user_id, user_name)values (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmtUser = conn.prepareStatement(insertUserSql, PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement pstmtAdmin = conn.prepareStatement(insertAdminSql)) {
            pstmtUser.setString(1, userName);
            pstmtUser.setString(2, "ADMIN");
            pstmtUser.setString(3, password);

            int affectedRows = pstmtUser.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmtUser.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);

                    pstmtAdmin.setInt(1, userId);
                    pstmtAdmin.setString(2, userName);

                    try {
                        pstmtAdmin.executeUpdate();
                        return true;
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("SQLState: " + e.getSQLState());
                        System.out.println("Error Code: " + e.getErrorCode());
                        System.out.println("Message: " + e.getMessage());
                        return false;
                    }
                }
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
