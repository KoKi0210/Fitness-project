
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestJDBCDriver {
    public static void main(String[] args) {
        try {
            // Зареждане на драйвъра (не е задължително за новите версии)
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC драйвърът е зареден успешно!");

            // Пробвай да се свържеш с база данни (ако имаш такава)
            String url = "jdbc:mysql://localhost:3306/testdb"; // Смени 'testdb' с името на твоята база
            String user = "root"; // Смени с твоя потребител
            String password = "082408   "; // Смени с твоята парола

            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                System.out.println("Успешно свързване с базата данни!");
            }

        } catch (ClassNotFoundException e) {
            System.out.println("Грешка: Драйвърът не е намерен!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Грешка при свързване с базата данни!");
            e.printStackTrace();
        }
    }
}
