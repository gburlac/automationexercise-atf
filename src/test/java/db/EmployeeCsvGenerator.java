package db;

import com.github.javafaker.Faker;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeCsvGenerator {
    private static final Faker faker = new Faker();

    public static class Employee {
        public String id;
        public String firstName;
        public String lastName;
        public Employee(String id, String firstName, String lastName) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    public static List<Employee> generateEmployees(int count) {
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String id = faker.number().digits(6);
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            employees.add(new Employee(id, firstName, lastName));
        }
        return employees;
    }

    public static void writeCsv(String filePath, List<Employee> employees) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("id,firstName,lastName\n");
            for (Employee emp : employees) {
                writer.write(emp.id + "," + emp.firstName + "," + emp.lastName + "\n");
            }
        }
    }

    // Example usage for 2 employees
    public static void main(String[] args) throws IOException {
        List<Employee> employees = generateEmployees(2);
        writeCsv("employees.csv", employees);
    }
}

