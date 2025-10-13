package pages;

import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import support.PropertyReader;

@Slf4j
public class HomePage extends BasePage {
    private static final String SIGNUP_LOGIN_LINK = "a[href='/login']";

    public HomePage(Page page){ super(page); }
    PropertyReader prop = new PropertyReader();
    public HomePage open(){
        log.info("Opening home page");
        page.navigate( PropertyReader.getTestProperty("baseUrl"));
        page.waitForLoadState();
        handleConsentIfPresent();
        log.debug("Home page loaded, current URL={}", page.url());
        return this;
    }

    public void goToLogin(){
        log.info("Navigating to login page");
        // Wait for the login link to be visible and enabled before clicking
        try {
            page.waitForSelector(SIGNUP_LOGIN_LINK, new Page.WaitForSelectorOptions().setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE).setTimeout(15000));
            boolean isEnabled = page.isEnabled(SIGNUP_LOGIN_LINK);
            boolean isVisible = page.isVisible(SIGNUP_LOGIN_LINK);
            log.debug("Login link visible: {}, enabled: {}", isVisible, isEnabled);
            if (!isVisible || !isEnabled) {
                throw new IllegalStateException("Login link is not visible or not enabled");
            }
            page.click(SIGNUP_LOGIN_LINK, new Page.ClickOptions().setTimeout(10000));
        } catch (Exception e) {
            log.error("Failed to click login link: {}", e.getMessage());
            throw e;
        }
        // Wait for the login page to load by waiting for the login email input
        page.waitForSelector("input[data-qa='login-email']", new Page.WaitForSelectorOptions().setTimeout(15000));
    }

    public void handleConsentIfPresent() {
        // Try the most robust selector: click the parent button of the <p class="fc-button-label">Consent</p>
        try {
            var consentButton = page.locator("button:has(p.fc-button-label:text('Consent'))");
            if (consentButton.isVisible()) {
                consentButton.click();
                log.info("Consent popup accepted by clicking parent button of <p class='fc-button-label'>Consent</p>");
                return;
            }
        } catch (Exception e) {
            log.warn("Failed to click parent button of consent label: {}", e.getMessage());
        }
        // Fallback: try previous selectors and text-based clicks
        String[] selectors = {
            "button:has-text('Consent')",
            "button:has-text('Accept')",
            "button[mode='primary']",
            "button[aria-label='Consent']",
            "button[aria-label='Accept']",
            ".fc-button-label",
            "p.fc-button-label"
        };
        for (String selector : selectors) {
            if (page.isVisible(selector)) {
                page.click(selector);
                log.info("Consent popup accepted with selector: {}", selector);
                return;
            }
        }
        // Try clicking by text if elements are not detected by selector
        page.locator(".fc-button-label, p.fc-button-label").all().forEach(el -> {
            String text = el.textContent();
            if (text != null && (text.trim().equalsIgnoreCase("Consent") || text.trim().equalsIgnoreCase("Accept"))) {
                el.click();
                log.info("Consent popup accepted by text: {} (fc-button-label)", text);
            }
        });
        page.locator("button").all().forEach(btn -> {
            String text = btn.textContent();
            if (text != null && (text.trim().equalsIgnoreCase("Consent") || text.trim().equalsIgnoreCase("Accept"))) {
                btn.click();
                log.info("Consent popup accepted by text: {}", text);
            }
        });
        log.debug("Consent popup not present or already handled.");
    }
}
