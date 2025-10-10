package pages;

import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HomePage extends BasePage {
    private static final String SIGNUP_LOGIN_LINK = "a[href='/login']";

    public HomePage(Page page){ super(page); }

    public HomePage open(){
        log.info("Opening home page");
        page.navigate("https://www.automationexercise.com/");
        page.waitForLoadState();
        log.debug("Home page loaded, current URL={}", page.url());
        return this;
    }

    public void goToLogin(){
        log.info("Navigating to login page");
        page.click(SIGNUP_LOGIN_LINK);
    }
}
