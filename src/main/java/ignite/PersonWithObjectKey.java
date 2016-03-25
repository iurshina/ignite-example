package ignite;

public class PersonWithObjectKey {

    private PersonKey id;
    private long balance;
    private long departmentType;

    public PersonKey getId() {
        return id;
    }

    public void setId(PersonKey id) {
        this.id = id;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public long getDepartmentType() {
        return departmentType;
    }

    public void setDepartmentType(long departmentType) {
        this.departmentType = departmentType;
    }
}
