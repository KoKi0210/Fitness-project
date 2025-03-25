public class Client {
    private String firstName;
    private String lastName;
    private String number;
    private int age;

    public Client(String firstName, String lastName, String number, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.number = number;
        this.age = age;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return (firstName + " " + lastName + " " + age + " " + number);
    }
}
