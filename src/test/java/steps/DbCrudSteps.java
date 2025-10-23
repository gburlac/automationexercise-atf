package steps;

import db.DynamicRegexContext;
import db.RegexDataGenerator;
import db.TestDataGenerators;
import db.TestDbConnection;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.Assertions;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbCrudSteps {
    private Connection connection;
    private List<Integer> importedEmployeeIds = new ArrayList<>();
    private List<String> importedEmployeeNames = new ArrayList<>();
    private List<String> importedEmployeeLastNames = new ArrayList<>();

    @Given("I connect to the database")
    public void i_connect_to_the_database() throws SQLException {
        connection = TestDbConnection.getConnection();
        Assertions.assertNotNull(connection, "DB connection should not be null");
    }

    @When("I insert an employee with SQL {string} using id method {string} and name method {string}")
    public void i_insert_employee_method(String sql, String idMethod, String nameMethod) throws SQLException {
        int employeeId = TestDataGenerators.invokeInt(idMethod);
        String employeeName = TestDataGenerators.invokeString(nameMethod);
        DynamicRegexContext.put("employeeId", employeeId);
        DynamicRegexContext.put("employeeName", employeeName);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            stmt.setString(2, employeeName);
            stmt.setQueryTimeout(TestDbConnection.getQueryTimeout() / 1000);
            int rows = stmt.executeUpdate();
            Assertions.assertEquals(1, rows, "Insert should affect 1 row");
        }
    }

    @Then("I verify employee exists with SQL {string} for generated id and name")
    public void i_verify_employee_exists_generated(String sql) throws SQLException {
        int employeeId = (Integer) DynamicRegexContext.get("employeeId");
        String employeeName = (String) DynamicRegexContext.get("employeeName");
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            stmt.setQueryTimeout(TestDbConnection.getQueryTimeout() / 1000);
            try (ResultSet rs = stmt.executeQuery()) {
                Assertions.assertTrue(rs.next(), "Employee should exist");
                Assertions.assertEquals(employeeName, rs.getString(1), "Employee name should match");
            }
        }
    }

    @When("I update the employee with SQL {string} using name method {string}")
    public void i_update_employee_method(String sql, String updatedNameMethod) throws SQLException {
        int employeeId = (Integer) DynamicRegexContext.get("employeeId");
        String updatedName = TestDataGenerators.invokeString(updatedNameMethod);
        DynamicRegexContext.put("updatedEmployeeName", updatedName);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, updatedName);
            stmt.setInt(2, employeeId);
            stmt.setQueryTimeout(TestDbConnection.getQueryTimeout() / 1000);
            int rows = stmt.executeUpdate();
            Assertions.assertEquals(1, rows, "Update should affect 1 row");
        }
    }

    @Then("I verify updated employee exists with SQL {string} for generated id and updated name")
    public void i_verify_employee_updated_generated(String sql) throws SQLException {
        int employeeId = (Integer) DynamicRegexContext.get("employeeId");
        String updatedName = (String) DynamicRegexContext.get("updatedEmployeeName");
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            stmt.setQueryTimeout(TestDbConnection.getQueryTimeout() / 1000);
            try (ResultSet rs = stmt.executeQuery()) {
                Assertions.assertTrue(rs.next(), "Employee should exist after update");
                Assertions.assertEquals(updatedName, rs.getString(1), "Updated name should match");
            }
        }
    }

    @When("I delete the employee with SQL {string}")
    public void i_delete_employee_generated(String sql) throws SQLException {
        int employeeId = (Integer) DynamicRegexContext.get("employeeId");
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            stmt.setQueryTimeout(TestDbConnection.getQueryTimeout() / 1000);
            int rows = stmt.executeUpdate();
            Assertions.assertEquals(1, rows, "Delete should affect 1 row");
        }
    }

    @Then("I verify employee does not exist with SQL {string}")
    public void i_verify_employee_not_exists_generated(String sql) throws SQLException {
        int employeeId = (Integer) DynamicRegexContext.get("employeeId");
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            stmt.setQueryTimeout(TestDbConnection.getQueryTimeout() / 1000);
            try (ResultSet rs = stmt.executeQuery()) {
                Assertions.assertFalse(rs.next(), "Employee should not exist after delete");
            }
        }
    }

    @Given("I generate a CSV file {string} with {int} employees using id method {string}, first name method {string}, and last name method {string}")
    public void generate_csv_file(String csvFile, int rowCount, String idMethod, String firstNameMethod, String lastNameMethod) throws IOException {
        importedEmployeeIds.clear();
        importedEmployeeNames.clear();
        importedEmployeeLastNames.clear();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            writer.write("employee_id,emp_firstname,emp_lastname\n");
            for (int i = 0; i < rowCount; i++) {
                int id = db.TestDataGenerators.invokeInt(idMethod);
                String firstName = db.TestDataGenerators.invokeString(firstNameMethod);
                String lastName = db.TestDataGenerators.invokeString(lastNameMethod);
                importedEmployeeIds.add(id);
                importedEmployeeNames.add(firstName);
                importedEmployeeLastNames.add(lastName);
                writer.write(id + "," + firstName + "," + lastName + "\n");
            }
        }
    }

    @When("I import the CSV file {string} into the hs_hr_employee table")
    public void import_csv_to_db(String csvFile) throws SQLException, IOException {
        if (connection == null || connection.isClosed()) {
            connection = db.TestDbConnection.getConnection();
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String header = reader.readLine(); // skip header
            String line;
            String sql = "INSERT INTO hs_hr_employee (employee_id, emp_firstname, emp_lastname) VALUES (?, ?, ?)";
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String firstName = parts[1];
                String lastName = parts[2];
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setInt(1, id);
                    stmt.setString(2, firstName);
                    stmt.setString(3, lastName);
                    stmt.setQueryTimeout(db.TestDbConnection.getQueryTimeout() / 1000);
                    stmt.executeUpdate();
                }
            }
        }
    }

    @Then("I verify {int} employees exist in the database for the imported ids, first names, and last names")
    public void verify_imported_employees(int expectedCount) throws SQLException {
        Assertions.assertEquals(expectedCount, importedEmployeeIds.size(), "Imported employee count should match");
        String sql = "SELECT emp_firstname, emp_lastname FROM hs_hr_employee WHERE employee_id = ?";
        int found = 0;
        for (int i = 0; i < importedEmployeeIds.size(); i++) {
            int id = importedEmployeeIds.get(i);
            String expectedFirstName = importedEmployeeNames.get(i);
            String expectedLastName = importedEmployeeLastNames.get(i);
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.setQueryTimeout(db.TestDbConnection.getQueryTimeout() / 1000);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && expectedFirstName.equals(rs.getString(1)) && expectedLastName.equals(rs.getString(2))) {
                        found++;
                    }
                }
            }
        }
        Assertions.assertEquals(expectedCount, found, "All imported employees should exist in DB");
    }

    @And("I delete the imported employees from the hs_hr_employee table")
    public void delete_imported_employees() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = db.TestDbConnection.getConnection();
        }
        String sql = "DELETE FROM hs_hr_employee WHERE employee_id = ?";
        for (int id : importedEmployeeIds) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.setQueryTimeout(db.TestDbConnection.getQueryTimeout() / 1000);
                stmt.executeUpdate();
            }
        }
        importedEmployeeIds.clear();
        importedEmployeeNames.clear();
        importedEmployeeLastNames.clear();
    }


    @After
    public void afterDbScenario(Scenario scenario) {
        StringBuilder data = new StringBuilder();
        data.append("employeeId=").append(DynamicRegexContext.get("employeeId")).append('\n');
        data.append("employeeName=").append(DynamicRegexContext.get("employeeName")).append('\n');
        data.append("updatedEmployeeName=").append(DynamicRegexContext.get("updatedEmployeeName")).append('\n');
        Allure.getLifecycle().addAttachment("DB Generated Data", "text/plain", "txt", data.toString().getBytes(StandardCharsets.UTF_8));
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception ignored) {
            }
            connection = null;
        }
        RegexDataGenerator.clearCache();
        DynamicRegexContext.clear();
    }
}
