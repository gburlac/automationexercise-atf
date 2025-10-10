package support;

import com.microsoft.playwright.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DriverManager {
    private static Playwright playwright;
    private static Browser browser;
    private static final ThreadLocal<BrowserContext> tlContext = new ThreadLocal<>();
    private static final ThreadLocal<Page> tlPage = new ThreadLocal<>();

    public static void start(boolean headless){
        log.info("Starting Playwright context (headless={})", headless);
        if (playwright == null) {
            playwright = Playwright.create();
            String browserType = System.getProperty("browser", System.getenv().getOrDefault("BROWSER", "chromium"));
            log.info("Launching browser: {}", browserType);
            switch (browserType.toLowerCase()) {
                case "firefox":
                    browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(headless));
                    break;
                case "webkit":
                    browser = playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(headless));
                    break;
                case "chromium":
                default:
                    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless));
                    break;
            }
            log.debug("{} browser launched", browserType);
        }
        BrowserContext context = browser.newContext();
        tlContext.set(context);
        Page newPage = context.newPage();
        tlPage.set(newPage);
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
        tlPage.remove(); tlContext.remove();
    }

    public static void shutdownAll(){
        log.info("Shutting down all Playwright resources");
        if (browser != null) { browser.close(); browser = null; log.debug("Browser closed"); }
        if (playwright != null) { playwright.close(); playwright = null; log.debug("Playwright closed"); }
    }
}
