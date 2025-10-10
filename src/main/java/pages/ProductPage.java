package pages;

import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import support.PropertyReader;

@Slf4j
public class ProductPage {
    private final Page page;
    private static final String PRODUCT_PAGE_URL = "/products";
    private static final String SEARCH_BOX_SELECTOR = "input[name='search']";
    private static final String SEARCH_BUTTON_SELECTOR = "button[type='submit']";
    private static final String PRODUCT_RESULT_SELECTOR = ".productinfo p";

    public ProductPage(Page page) {
        this.page = page;
    }

    public void navigateTo() {
        String baseUrl = PropertyReader.getProperty("baseUrl");
        String fullUrl = baseUrl.endsWith("/") ? baseUrl + "products" : baseUrl + "/products";
        page.navigate(fullUrl);
        log.info("Navigated to product page: {}", fullUrl);
    }

    public void searchForProduct(String product) {
        page.fill(SEARCH_BOX_SELECTOR, product);
        page.click(SEARCH_BUTTON_SELECTOR);
        log.info("Searched for product: {}", product);
    }

    public boolean isProductResultVisible(String product) {
        boolean visible = page.locator(PRODUCT_RESULT_SELECTOR + ":has-text('" + product + "')").isVisible();
        log.info("Product result for '{}' visible: {}", product, visible);
        return visible;
    }
}
