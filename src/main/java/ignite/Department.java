package ignite;

public class Department {

    private long departmentType;

    public Department(long departmentType) {
        this.departmentType = departmentType;
    }

    public long getDepartmentType() {
        return departmentType;
    }

    public void setDepartmentType(long departmentType) {
        this.departmentType = departmentType;
    }
}
