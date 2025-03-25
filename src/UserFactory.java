import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class UserFactory {

//    public User createUser(String userName, String password, UserType userType) {
//        if (userType.equals(UserType.ADMIN)) {
//            return new Admin(userName, password);
//        } else if (userType.equals(UserType.EMPLOYEE)) {
//            return new Employee(userName, password);
//        } else return null;
//    }

    public UserType checkUserIfExist(String userName, String password, String dbUrl, String dbUser, String dbPass){

        String sql = "select role from users where name= ? and password = ?";

        try(Connection conn = DriverManager.getConnection(dbUrl,dbUser,dbPass);
            PreparedStatement preparedStatement = conn.prepareStatement(sql))
        {
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2,password);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()){
                if (rs.getString("role").equals("ADMIN")){
                    return UserType.ADMIN;

                } else if (rs.getString("role").equals("EMPLOYEE")) {
                    return UserType.EMPLOYEE;
                }

            }

        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }

        return null;
    }

    public int checkEmployeeId(String userName, String password, String dbUrl, String dbUser, String dbPass){
        String sql = "select user_id from users where name= ? and password = ?";
        String sql1 = "select employee_id from employees where user_id = ?";

        try(Connection conn = DriverManager.getConnection(dbUrl,dbUser,dbPass);
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            PreparedStatement preparedStatement1 = conn.prepareStatement(sql1))
        {
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2,password);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()){

                preparedStatement1.setInt(1,rs.getInt(1));
                ResultSet rs1 = preparedStatement1.executeQuery();
                if (rs1.next()) {
                    return rs1.getInt(1);
                }
            }

        }catch (SQLException e){
            e.printStackTrace();

        }

        return 0;
    }


    public UserType checkUserType(String userName, String password){
        try(BufferedReader reader = new BufferedReader(new FileReader("Users.txt"))){
            String line;
            while ((line = reader.readLine())!= null){
                String[] info = line.split("\\s+");
                if (info[0].equals(userName)&& info[1].equals(password)){
                    if (info[2].equalsIgnoreCase("ADMIN")){
                        return UserType.ADMIN;
                    } else if (info[2].equalsIgnoreCase("EMPLOYEE")) {
                        return UserType.EMPLOYEE;
                    }

                }
            }
        }catch (IOException e){
            System.out.println("Проблем с четене на файла! "+ e.getMessage());
        }
        return null;
    }
}
