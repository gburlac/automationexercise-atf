package steps;

import com.microsoft.playwright.Page;
import io.cucumber.java.en.*;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import pages.HomePage;
import pages.LoginPage;
import support.DriverManager;

@Slf4j
public class LoginSteps {
    private Page page;
    private HomePage home;
    private LoginPage login;

    @Given("I am on the home page")
    public void i_am_on_home_page() {
        log.info("Opening home page for login scenario");
        page = DriverManager.page();
        home = new HomePage(page).open();
    }

    @When("I navigate to the login page")
    public void i_navigate_to_login_page() {
        log.info("Navigating to login page");
        home.goToLogin();
        login = new LoginPage(page);
    }

    @When("I login with email {string} and password {string}")
    public void i_login_with(String email, String pass) {
        log.info("Submitting login form email={} ", email);
        login.login(email, pass);
    }

    @Then("I should see an invalid login error")
    public void i_should_see_error() {
        log.info("Verifying invalid login error is displayed");
        Assertions.assertThat(login.isErrorVisible()).isTrue();
    }
}
