import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;

public class EmployeeStats implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private int registrationCount;
    private Month month;

    public EmployeeStats(String name) {
        this.name = name;
        this.registrationCount = 0;
        this.month = LocalDate.now().getMonth();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRegistrationCount() {
        return registrationCount;
    }

    public void setRegistrationCount(int registrationCount) {
        this.registrationCount = registrationCount;
    }

    public Month getMonth() {
        return month;
    }

    @Override
    public String toString() {
        return (name + " " + registrationCount + " " + month);
    }
}
