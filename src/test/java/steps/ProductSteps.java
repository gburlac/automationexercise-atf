package steps;

import com.microsoft.playwright.Page;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import pages.ProductPage;
import support.DriverManager;

@Slf4j
public class ProductSteps {
    private Page page;
    private ProductPage productPage;

    @Given("I am on the product page")
    public void i_am_on_the_product_page() {
        page = DriverManager.page();
        productPage = new ProductPage(page);
        productPage.navigateTo();
        log.info("Navigated to the product page");
    }

    @When("I search for {string}")
    public void i_search_for_product(String product) {
        productPage.searchForProduct(product);
        log.info("Searched for product: {}", product);
    }

    @Then("I should see results for {string}")
    public void i_should_see_results_for_product(String product) {
        boolean visible = productPage.isProductResultVisible(product);
        log.info("Verifying search results for product: {} - visible: {}", product, visible);
        org.assertj.core.api.Assertions.assertThat(visible).isTrue();
    }
}
