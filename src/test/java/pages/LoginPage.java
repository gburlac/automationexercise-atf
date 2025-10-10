package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginPage extends BasePage {
    private final Locator email = page.locator("input[data-qa='login-email']");
    private final Locator password = page.locator("input[placeholder='Password']");
    private final Locator loginBtn = page.locator("button[data-qa='login-button']");
    private final Locator error = page.locator("p:has-text('Your email or password is incorrect!')");

    public LoginPage(Page page){ super(page); }

    public void login(String user, String pass){
        log.info("Attempting login for user={} ", user);
        email.fill(user);
        password.fill(pass);
        loginBtn.click();
        log.debug("Login submitted for user={}", user);
    }

    public boolean isErrorVisible(){
        boolean visible = error.isVisible();
        if (visible) {
            log.warn("Login error message displayed");
        } else {
            log.debug("Login error message not displayed");
        }
        return visible;
    }
}
