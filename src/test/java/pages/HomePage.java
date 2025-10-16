package pages;

import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import support.PropertyReader;

@Slf4j
public class HomePage extends BasePage {
    private static final String SIGNUP_LOGIN_LINK = "a[href='/login']";
    private static final long DEFAULT_TIMEOUT_MS = Long.parseLong(PropertyReader.getTestProperty("defaultTimeoutMs"));
    private static final long INPUT_TIMEOUT_MS = Long.parseLong(PropertyReader.getTestProperty("inputTimeoutMs"));

    public HomePage(Page page){ super(page); }
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
        try {
            page.waitForSelector(SIGNUP_LOGIN_LINK, new Page.WaitForSelectorOptions().setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE).setTimeout(DEFAULT_TIMEOUT_MS));
            boolean isEnabled = page.isEnabled(SIGNUP_LOGIN_LINK);
            boolean isVisible = page.isVisible(SIGNUP_LOGIN_LINK);
            log.debug("Login link visible: {}, enabled: {}", isVisible, isEnabled);
            if (!isVisible || !isEnabled) {
                throw new IllegalStateException("Login link is not visible or not enabled");
            }
            page.click(SIGNUP_LOGIN_LINK, new Page.ClickOptions().setTimeout(DEFAULT_TIMEOUT_MS));
        } catch (Exception e) {
            log.error("Failed to click login link: {}", e.getMessage());
            throw e;
        }
        page.waitForSelector("input[data-qa='login-email']", new Page.WaitForSelectorOptions().setTimeout(INPUT_TIMEOUT_MS));
    }

    public void handleConsentIfPresent() {
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
