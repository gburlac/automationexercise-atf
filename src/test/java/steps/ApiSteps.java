package steps;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import support.PropertyReader;

@Slf4j
public class ApiSteps {
    private static Playwright pw;
    private static APIRequestContext api;
    private static APIResponse response;
    static {
        PropertyReader.loadProperties("config/test.properties");
    }


    @Given("the AutomationExercise API is available")
    public void the_api_is_available() {
        String baseUrl = PropertyReader.getProperty("baseUrlApi");
        if (baseUrl == null || baseUrl.isEmpty()) {
            throw new IllegalStateException("baseUrlApi property is not set");
        }
        log.info("Initializing API context baseUrl={}", baseUrl);
        pw = Playwright.create();
        api = pw.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(baseUrl)
                .setExtraHTTPHeaders(java.util.Map.of(
                        "User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124 Safari/537.36",
                        "Accept", "*/*"
                )));
    }

    @When("I send a GET request to {string}")
    public void i_send_a_get_request_to(String endpoint) {
        String baseUrl = PropertyReader.getProperty("baseUrlApi");

        String fullUrl = endpoint.startsWith("http") ? endpoint : baseUrl + (endpoint.startsWith("/") ? endpoint : "/" + endpoint);
        log.info("GET {}", fullUrl);
        response = api.get(fullUrl);
        log.debug("Response status={} length={}", response.status(), response.text().length());
    }

    @When("I send a POST request to {string} with email {string} and password {string}")
    public void i_send_a_post_request_to_with_email_and_password(String endpoint, String email, String password) {
        log.info("POST {} (email={})", endpoint, email);
        FormData form = FormData.create()
                .set("email", email)
                .set("password", password);
        response = api.post(endpoint, RequestOptions.create().setForm(form));
        log.info("POST to {} completed", endpoint);
        log.debug("POST response status={}", response.status());
    }

    @Then("the response status should be {int}")
    public void the_response_status_should_be(Integer expected) {
        int actual = response.status();
        if (actual != expected) {
            String body = response.text();
            log.error("Status mismatch expected={} actual={} bodySnippet={}", expected, actual,
                    body.substring(0, Math.min(body.length(), 300)));
        } else {
            log.info("Status assertion passed {}", actual);
        }
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @And("the response should contain {string}")
    public void the_response_should_contain(String expectedText) {
        String body = response.text();
        log.debug("Asserting body contains snippet='{}'", expectedText);
        Assertions.assertThat(body).contains(expectedText);
    }

}
