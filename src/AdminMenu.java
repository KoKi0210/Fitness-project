import java.util.Scanner;

public class AdminMenu {

    private static void displayAdminMenu(){
        System.out.println("              Меню");
        System.out.println("Изберете една от следните опции:");
        System.out.println("1. Регистрация на нов потребител");
        System.out.println("2. Справка информация за служители");
        System.out.println("3. Изтриване на потребител");
        System.out.println("4. Изход");
        System.out.print("Въведете число: ");
    }

    public static void chooseFromAdminMenu(Scanner scanner,String dbUrl, String dbName, String dbPass){
        AdminFactory adminFactory = new AdminFactory(scanner, dbUrl,  dbName,  dbPass);
        boolean isWorking = true;
        while (isWorking){
            displayAdminMenu();

            int option = Integer.parseInt(scanner.nextLine());
            switch (option){
                case 1:
                    adminFactory.registerUser();
                    break;
                case 2:
                    adminFactory.checkEmployeeStats();
                    break;
                case 3:
                   adminFactory.removeUser();
                    break;
                case 4:
                    isWorking = false;
                    System.out.println("Връщане на главно меню");
                    break;
                default:
                    System.out.println("Няма такъв избор! Въведете валидно число.");
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Проблем при зареждане " + e.getMessage());
            }
        }
    }
}
