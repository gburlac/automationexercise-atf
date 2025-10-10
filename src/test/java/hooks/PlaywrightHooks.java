package hooks;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import support.DriverManager;
import support.AllureEnvironmentWriter;

import java.nio.charset.StandardCharsets;

@Slf4j
public class PlaywrightHooks {

    @Before
    public void beforeScenario() {
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "true"));
        log.info("Starting scenario with headless={} ", headless);
        DriverManager.start(headless);
    }

    @After
    public void afterScenario(Scenario scenario) {
        log.info("Finishing scenario: {} - status={}", scenario.getName(), scenario.getStatus());
        // Attach screenshot on failure
        if (scenario.isFailed()) {
            log.warn("Scenario failed, capturing screenshot");
            try {
                byte[] screenshot = DriverManager.page().screenshot();
                Allure.getLifecycle().addAttachment("Failure Screenshot", "image/png", "png", screenshot);
            } catch (Exception e) {
                String msg = "Could not capture screenshot: " + e.getMessage();
                log.error(msg, e);
                Allure.getLifecycle().addAttachment("Screenshot Error", "text/plain", "txt", msg.getBytes(StandardCharsets.UTF_8));
            }
        }
        DriverManager.stop();
    }

    @AfterAll
    public static void afterAll() {
        log.info("Global teardown: shutting down all drivers and writing Allure environment");
        DriverManager.shutdownAll();
        // Write environment metadata for Allure report (once per run)
        AllureEnvironmentWriter.write();
    }
}
