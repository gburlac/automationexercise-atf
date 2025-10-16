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
    public void beforeScenario(Scenario scenario) {
        support.PropertyReader.loadTestProperties();
        // Only start browser for @ui scenarios
        if (scenario.getSourceTagNames().contains("@ui")) {
            String headlessStr = support.PropertyReader.getTestProperty("headless");
            boolean headless = true;
            if (headlessStr != null && !headlessStr.isEmpty()) {
                headless = Boolean.parseBoolean(headlessStr);
            }
            log.info("Starting scenario with headless={} ", headless);
            DriverManager.start(headless);
        } else {
            log.info("Skipping browser startup for non-UI scenario: {}", scenario.getName());
        }
    }

    @After
    public void afterScenario(Scenario scenario) {
        log.info("Finishing scenario: {} - status={}", scenario.getName(), scenario.getStatus());
        // Read screenshotOnFailure property
        support.PropertyReader.loadTestProperties();
        String screenshotOnFailureStr = support.PropertyReader.getTestProperty("screenshotOnFailure");
        boolean screenshotOnFailure = true;
        if (screenshotOnFailureStr != null && !screenshotOnFailureStr.isEmpty()) {
            screenshotOnFailure = Boolean.parseBoolean(screenshotOnFailureStr);
        }
        // Attach screenshot on failure if enabled
        if (scenario.isFailed() && screenshotOnFailure) {
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
        AllureEnvironmentWriter.write();
    }
}
