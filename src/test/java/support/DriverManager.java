package support;

import com.microsoft.playwright.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DriverManager {
    private static Playwright playwright;
    private static final ThreadLocal<BrowserContext> tlContext = new ThreadLocal<>();
    private static final ThreadLocal<Page> tlPage = new ThreadLocal<>();
    private static final ThreadLocal<Browser> tlBrowser = new ThreadLocal<>();

    public static void start(boolean headless){
        log.info("Starting Playwright context (headless={}) in thread: {}", headless, Thread.currentThread().getName());
        if (playwright == null) {
            playwright = Playwright.create();
            // Always load properties from file before reading slowMo and browser
            support.PropertyReader.loadTestProperties();
        }
        // Each thread gets its own browser
        Browser browser = tlBrowser.get();
        if (browser == null) {
            String browserType = support.PropertyReader.getTestProperty("browser");
            if (browserType == null || browserType.isEmpty()) {
                browserType = System.getProperty("browser", System.getenv().getOrDefault("BROWSER", "chromium"));
            }
            String slowMoStr = support.PropertyReader.getTestProperty("slowMo");
            int slowMo = 0;
            try {
                if (slowMoStr != null && !slowMoStr.isEmpty()) {
                    slowMo = Integer.parseInt(slowMoStr);
                }
            } catch (NumberFormatException e) {
                log.warn("Invalid slowMo value '{}', using default 0", slowMoStr);
            }
            log.info("Launching browser: {} with slowMo={}ms", browserType, slowMo);
            BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(headless).setSlowMo(slowMo);
            switch (browserType.toLowerCase()) {
                case "firefox":
                    browser = playwright.firefox().launch(options);
                    break;
                case "webkit":
                    browser = playwright.webkit().launch(options);
                    break;
                case "chromium":
                case "chrome":
                case "edge":
                default:
                    browser = playwright.chromium().launch(options);
                    break;
            }
            tlBrowser.set(browser);
            log.debug("{} browser launched for thread {}", browserType, Thread.currentThread().getName());
        }
        BrowserContext context = browser.newContext();
        tlContext.set(context);
        Page newPage = context.newPage();
        tlPage.set(newPage);
        // Set consent cookie to prevent popup (update name/value if needed)
        try {
            com.microsoft.playwright.options.Cookie consentCookie = new com.microsoft.playwright.options.Cookie("fcConsent", "1"); // Update name/value as needed
            consentCookie.setDomain("automationexercise.com");
            consentCookie.setPath("/");
            context.addCookies(java.util.List.of(consentCookie));
            log.info("Consent cookie set to prevent popup");
            // Optionally reload the page to ensure cookie is applied
            newPage.reload();
        } catch (Exception e) {
            log.warn("Could not set consent cookie: {}", e.getMessage());
        }
        log.debug("New browser context and page created");
    }

    public static Page page(){
        Page p = tlPage.get();
        if (p == null) {
            log.warn("Requested page but none is stored in ThreadLocal");
        }
        return p;
    }

    public static void stop(){
        BrowserContext ctx = tlContext.get();
        if (ctx != null) {
            log.info("Closing browser context");
            ctx.close();
        }
        Page p = tlPage.get();
        if (p != null) {
            log.debug("Removing page from ThreadLocal");
        }
        Browser browser = tlBrowser.get();
        if (browser != null) {
            log.info("Closing browser for thread {}", Thread.currentThread().getName());
            browser.close();
            tlBrowser.remove();
        }
        tlPage.remove(); tlContext.remove();
    }

    public static void shutdownAll(){
        log.info("Shutting down all Playwright resources");
        // Only close playwright, browsers are closed per thread in stop()
        if (playwright != null) { playwright.close(); playwright = null; log.debug("Playwright closed"); }
    }
}
