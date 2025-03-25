import java.util.Scanner;

public class EmployeeMenu {

    private static void displayEmployeeMenu() {
        System.out.println("              Меню");
        System.out.println("Изберете една от следните опции:");
        System.out.println("1. Регистрация на нов клиент");
        System.out.println("2. Вход");
        System.out.println("3. Справка присъствие");
        System.out.println("4. Изтриване на клиент");
        System.out.println("5. Обновяване на данни на клиент");
        System.out.println("6. Нулиране на присъствия на клиент");
        System.out.println("7. Списък на клиенти");
        System.out.println("8. Изход");
        System.out.print("Въведете число: ");

    }

    public static void chooseFromEmployeeMenu(int employee_id, Scanner scanner, String dbUrl, String dbName, String dbPass) {
        EmployeeFactory employeeFactory = new EmployeeFactory(dbUrl, dbName, dbPass, scanner);
        boolean isWorking = true;
        while (isWorking) {
            displayEmployeeMenu();

            int option = Integer.parseInt(scanner.nextLine());
            try {
                switch (option) {
                    case 1:
                        employeeFactory.registerClient(employee_id);
                        break;
                    case 2:
                        employeeFactory.clientLogin();
                        break;
                    case 3:
                        employeeFactory.checkPresences();
                        break;
                    case 4:
                        employeeFactory.removeClient();
                        break;
                    case 5:
                        employeeFactory.changeClientInfo();
                        break;
                    case 6:
                        employeeFactory.resetPresences();
                        break;
                    case 7:
                        employeeFactory.showAllClients();
                        break;
                    case 8:
                        isWorking = false;
                        System.out.println("Връщане на главно меню");
                        break;
                    default:
                        System.out.println("Няма такъв избор! Въведете валидно число.");
                }
            } catch (PresencesOverException e) {
                System.out.println("Внимание: " + e.getMessage());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Проблем при зареждане " + e.getMessage());
            }
        }
    }
}
