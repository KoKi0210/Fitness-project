public class Employee extends User{

    public Employee(String userName, String password) {
        super(userName, password);
    }

    @Override
    public UserType getUserType() {
        return UserType.EMPLOYEE;
    }

    @Override
    public String toString() {
        return (getUserName() + " " + getPassword() + " " + getUserType());
    }
}
