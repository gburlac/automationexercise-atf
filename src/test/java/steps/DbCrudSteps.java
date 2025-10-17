package steps;

import db.TestDbConnection;
import db.TestDataGenerators;
import db.DynamicRegexContext;
import db.RegexDataGenerator;
import io.cucumber.java.en.*;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.Assertions;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class DbCrudSteps {
    private Connection connection;

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

    @After
    public void afterDbScenario(Scenario scenario) {
        StringBuilder data = new StringBuilder();
        data.append("employeeId=").append(DynamicRegexContext.get("employeeId")).append('\n');
        data.append("employeeName=").append(DynamicRegexContext.get("employeeName")).append('\n');
        data.append("updatedEmployeeName=").append(DynamicRegexContext.get("updatedEmployeeName")).append('\n');
        Allure.getLifecycle().addAttachment("DB Generated Data", "text/plain", "txt", data.toString().getBytes(StandardCharsets.UTF_8));
        if (connection != null) { try { connection.close(); } catch (Exception ignored) {} connection = null; }
        RegexDataGenerator.clearCache();
        DynamicRegexContext.clear();
    }
}
