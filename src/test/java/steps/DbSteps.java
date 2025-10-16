package steps;

import io.cucumber.java.en.*;
import util.DbConnectionUtil;
import java.sql.*;
import static org.assertj.core.api.Assertions.*;

public class DbSteps {
    private Connection connection;

    @Given("I connect to the database")
    public void i_connect_to_the_database() throws SQLException {
        connection = DbConnectionUtil.getConnection();
        assertThat(connection).isNotNull();
        assertThat(connection.isClosed()).isFalse();
    }

    @When("I insert an employee with SQL {string} and id {string} and name {string}")
    public void i_insert_employee_with_sql(String sql, String id, String name) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(id));
            stmt.setString(2, name);
            stmt.executeUpdate();
        }
    }

    @Then("I verify employee exists with SQL {string} and id {string} and name {string}")
    public void i_verify_employee_exists_with_sql(String sql, String id, String name) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(id));
            try (ResultSet rs = stmt.executeQuery()) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getString(1)).isEqualTo(name);
            }
        }
    }

    @When("I update the employee with SQL {string} and name {string} and id {string}")
    public void i_update_employee_with_sql(String sql, String updatedName, String id) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, updatedName);
            stmt.setInt(2, Integer.parseInt(id));
            stmt.executeUpdate();
        }
    }

    @When("I delete the employee with SQL {string} and id {string}")
    public void i_delete_employee_with_sql(String sql, String id) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(id));
            stmt.executeUpdate();
        }
    }

    @Then("I verify employee does not exist with SQL {string} and id {string}")
    public void i_verify_employee_does_not_exist_with_sql(String sql, String id) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(id));
            try (ResultSet rs = stmt.executeQuery()) {
                assertThat(rs.next()).isFalse();
            }
        }
    }
}
