import java.sql.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmployeeFactory {

    private final String dbUrl;
    private final String dbName;
    private final String dbPass;
    private final Scanner scanner;
    private static final String NUMBER_REGEX = "^(088|089|098)\\d{7}$";
    private static final Pattern pattern = Pattern.compile(NUMBER_REGEX);

    public EmployeeFactory(String dbUrl, String dbName, String dbPass, Scanner scanner) {
        this.dbUrl = dbUrl;
        this.dbName = dbName;
        this.dbPass = dbPass;
        this.scanner = scanner;
    }

    public void registerClient(int employee_id) {

        System.out.print("Въведете номер за регистрация: ");
        String number = scanner.nextLine();
        if (isPhoneNumberRegistered(number)) {
            return;
        }

        System.out.print("Въведете име:");
        String first_name = scanner.nextLine();
        System.out.print("Въведете фамилия: ");
        String last_name = scanner.nextLine();
        System.out.print("Въведете години: ");
        int age = Integer.parseInt(scanner.nextLine());

        String insertUserSql = "insert into users (name,role,password)values (?, ?, ?)";
        String insertCustomerSql = "insert into customers (user_id, first_name, last_name, age, number)values (?,?,?,?,?)";
        String increaseRegCountSql = "update employee_registrations set registration_count = registration_count + 1 where employee_id = ?";
        String insertCustomerToEmployeeSql = "insert into employee_customer_registrations (employee_id, customer_id)values (?,?)";
        String createPresenceSql = "insert into presences (customer_id, number)values (?,?)";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbName, dbPass);
             PreparedStatement prsUser = conn.prepareStatement(insertUserSql, PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement prsCustomer = conn.prepareStatement(insertCustomerSql, PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement prcIncreaseRegCount = conn.prepareStatement(increaseRegCountSql);
             PreparedStatement prcCusToEmp = conn.prepareStatement(insertCustomerToEmployeeSql);
             PreparedStatement prsCreatePresence = conn.prepareStatement(createPresenceSql)) {

            prsUser.setString(1, first_name);
            prsUser.setString(2, "CUSTOMER");
            prsUser.setString(3, "");

            int affectedRows = prsUser.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = prsUser.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);

                    prsCustomer.setInt(1, userId);
                    prsCustomer.setString(2, first_name);
                    prsCustomer.setString(3, last_name);
                    prsCustomer.setInt(4, age);
                    prsCustomer.setString(5, number);

                    affectedRows = prsCustomer.executeUpdate();

                }
            }

            if (affectedRows > 0) {
                ResultSet generatedKeys = prsCustomer.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int customer_id = generatedKeys.getInt(1);

                    prcIncreaseRegCount.setInt(1, employee_id);
                    prcIncreaseRegCount.executeUpdate();

                    prcCusToEmp.setInt(1, employee_id);
                    prcCusToEmp.setInt(2, customer_id);
                    prcCusToEmp.executeUpdate();

                    prsCreatePresence.setInt(1, customer_id);
                    prsCreatePresence.setString(2, number);
                    prsCreatePresence.executeUpdate();

                    System.out.println("Успешна регистрация!");

                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public boolean isPhoneNumberRegistered(String phoneNumber) {
        String sql = "Select exists(select 1 from customers where number = ?)";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbName, dbPass);
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, phoneNumber);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next() && rs.getBoolean(1)) {
                System.out.println("Има регистрация с този номер!");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void removeClient() {
        System.out.print("Въведете номер за изтриване на клиент: ");
        String number = scanner.nextLine();
        if (checkNumber(number)) {
            return;
        }
        int userId = 0;
        String checkUserIdSql = "select user_id from customers where number = ?";
        try (Connection connection = DriverManager.getConnection(dbUrl, dbName, dbPass);
             PreparedStatement preparedStatement = connection.prepareStatement(checkUserIdSql)) {
            preparedStatement.setString(1, number);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                userId = rs.getInt("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String removeClientSql = "delete from users where user_id = ?";
        try (Connection connection = DriverManager.getConnection(dbUrl, dbName, dbPass);
             PreparedStatement preparedStatement = connection.prepareStatement(removeClientSql)) {
            preparedStatement.setInt(1, userId);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Успешно премахнат потребител!");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Грешка! Потребителят не е премахнат!");
    }

    public void changeClientInfo() {
        System.out.print("Въведете номер за смяна на данни: ");
        String number = scanner.nextLine();
        if (checkNumber(number)) {
            return;
        }

        System.out.print("Въведете нов номер: ");
        String newNumber = scanner.nextLine();
        Matcher matcher = pattern.matcher(newNumber);

        while (!matcher.matches() || newNumber.equals(number)) {
            System.out.println("Въведения номер е грешен, чуждестранен или еднакъв със стария!");
            System.out.print("Въведе номер: ");
            newNumber = scanner.nextLine();
            matcher = pattern.matcher(newNumber);
        }

        String changeNumberInCustomerSql = "update customers set number = ? where number = ?";
        String changeNumberInPresencesSql = "update presences set number = ? where number = ?";

        try (Connection connection = DriverManager.getConnection(dbUrl, dbName, dbPass);
             PreparedStatement stm1 = connection.prepareStatement(changeNumberInCustomerSql);
             PreparedStatement stm2 = connection.prepareStatement(changeNumberInPresencesSql)) {

            stm1.setString(1, newNumber);
            stm1.setString(2, number);
            stm2.setString(1, newNumber);
            stm2.setString(2, number);

            int result1 = stm1.executeUpdate();
            int result2 = stm2.executeUpdate();

            if (result1 > 0 && result2 > 0) {
                System.out.println("Номерът е сменен успешно!");
                return;
            }
            System.out.println("Грешка! Номерът не е сменен!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void checkPresences() {
        System.out.print("Въведете номер за справка: ");
        String number = scanner.nextLine();
        if (checkNumber(number)) {
            return;
        }
        boolean isFound = false;
        String checkPresenceSql = "select presence_count from presences where number = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbName, dbPass);
             PreparedStatement preparedStatement = conn.prepareStatement(checkPresenceSql)) {
            preparedStatement.setString(1, number);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                int presences = rs.getInt("presence_count");
                System.out.println("Брой присъствия: " + presences);
                isFound = true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!isFound) {
            System.out.println("Номерът не е намерен!");
        }
    }

    public void resetPresences() {
        System.out.print("Въведете номер за нулиране на присъствия: ");
        String number = scanner.nextLine();
        if (checkNumber(number)) {
            return;
        }
        String setPresenceToOne = "update presences set presence_count = 1 where number = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbName, dbPass);
             PreparedStatement preparedStatement = conn.prepareStatement(setPresenceToOne)) {
            preparedStatement.setString(1, number);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Присъствията са подновени!");
                return;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Неуспешно подновяване!");
    }

    public void clientLogin() throws PresencesOverException {
        System.out.print("Въведете номер за вход: ");
        String number = scanner.nextLine();

        if (checkNumber(number)) {
            return;
        }
        presenceValidation(number);

    }

    public void showAllClients() {
        String selectClientsSql = "select * from customers";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbName, dbPass);
             PreparedStatement statement = conn.prepareStatement(selectClientsSql)) {
            ResultSet rs = statement.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("Няма намерени потребители!");
            } else {

                System.out.println("       Списък на потребители");
                System.out.println("id|Име|Фамилия|Години|Номер");
                while (rs.next()) {
                    int id = rs.getInt("customer_id");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    int age = rs.getInt("age");
                    String number = rs.getString("number");
                    System.out.println(id + " " + firstName + " " + lastName + " " + age + " " + number);

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void presenceValidation(String number) {
        String checkPresencesCountSql = "select presence_count as count from presences where number = ?";
        String increasePresencesCountSql = "update presences set presence_count = presence_count + 1 where number = ?";
        String setPresenceToOne = "update presences set presence_count = 1 where number = ?";
        int presenceCount = 0;

        try (Connection conn = DriverManager.getConnection(dbUrl, dbName, dbPass);
             PreparedStatement prsPresenceCount = conn.prepareStatement(checkPresencesCountSql)) {

            prsPresenceCount.setString(1, number);
            ResultSet rs = prsPresenceCount.executeQuery();
            if (rs.next()) {
                presenceCount = rs.getInt("count");
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


        boolean isValid = true;
        try (Connection conn = DriverManager.getConnection(dbUrl, dbName, dbPass)) {
            if (presenceCount > 10) {
                System.out.println("Направили сте 10 посещения. Желаете ли да си подновите абонамента?(Да/Не)");
                String answer = scanner.nextLine();
                if (answer.equalsIgnoreCase("Да")) {
                    try (PreparedStatement prs = conn.prepareStatement(setPresenceToOne)) {
                        prs.setString(1, number);
                        int affectedRows = prs.executeUpdate();
                        if (affectedRows > 0) {
                            System.out.println("Абонаментът е подновен!");
                        }
                    }

                } else {
                    isValid = false;
                    throw new PresencesOverException("Абонамента не е подновен");
                }
            } else if (presenceCount == 10) {
                System.out.println("Последно посещение!");
                try (PreparedStatement prs = conn.prepareStatement(increasePresencesCountSql)) {
                    prs.setString(1, number);
                    int affectedRows = prs.executeUpdate();
                    if (affectedRows == 0) {
                        isValid = false;
                    }
                }
            } else {
                try (PreparedStatement prs = conn.prepareStatement(increasePresencesCountSql)) {
                    prs.setString(1, number);
                    int affectedRows = prs.executeUpdate();
                    if (affectedRows == 0) {
                        isValid = false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (isValid) {
            System.out.println("Присъствие маркирано.");
        }
    }

    private boolean checkNumber(String num) {
        Matcher matcher = pattern.matcher(num);
        if (!matcher.matches()) {
            System.out.println("Грешен номер!");
            return true;
        }
        return false;
    }

}

