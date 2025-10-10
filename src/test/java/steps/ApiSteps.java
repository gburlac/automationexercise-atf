package steps;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;
import io.cucumber.java.After;
import io.cucumber.java.en.*;
import org.assertj.core.api.Assertions;

import java.util.Map;

public class ApiSteps {
    private static Playwright playwright;
    private static APIRequestContext api;
    private static APIResponse response;

    @Given("the AutomationExercise API is available")
    public void the_api_is_available() {
        playwright = Playwright.create();
        api = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL("https://automationexercise.com/api"));
    }

    // ---------- GET ----------
    @When("I send a GET request to {string}")
    public void i_send_a_get_request_to(String endpoint) {
        response = api.get(endpoint);
    }

    // ---------- POST ----------
    @When("I send a POST request to {string} with email {string} and password {string}")
    public void i_send_a_post_request_to_with_email_and_password(String endpoint, String email, String password) {
        response = api.post(endpoint,
                RequestOptions.create().setForm((FormData) Map.of("email", email, "password", password)));
    }

    // ---------- Assertions ----------
    @Then("the response status should be {int}")
    public void the_response_status_should_be(Integer expectedStatus) {
        Assertions.assertThat(response.status()).isEqualTo(expectedStatus);
    }

    @And("the response should contain {string}")
    public void the_response_should_contain(String expectedText) {
        Assertions.assertThat(response.text()).contains(expectedText);
    }

    @After
    public void tearDown() {
        if (api != null) api.dispose();
        if (playwright != null) playwright.close();
    }
}
