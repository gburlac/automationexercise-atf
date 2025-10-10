package support;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * Writes environment.properties into the Allure results directory so the data
 * appears in the Allure report (Behaviors > Environment). Executed once in @AfterAll.
 */
@Slf4j
public class AllureEnvironmentWriter {
    public static void writeEnvironment(Map<String, String> environment, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (Map.Entry<String, String> entry : environment.entrySet()) {
                writer.write(String.format("%s=%s\n", entry.getKey(), entry.getValue()));
            }
            log.info("Allure environment.properties written to {}", filePath);
        } catch (IOException e) {
            log.error("Failed to write Allure environment.properties: {}", e.getMessage());
        }
    }

    // Add this static method for PlaywrightHooks
    public static void write() {
        Map<String, String> env = new HashMap<>();
        env.put("Browser", System.getProperty("browser", "chromium"));
        env.put("Headless", System.getProperty("headless", "true"));
        env.put("BaseUrl", System.getProperty("baseUrl", "https://automationexercise.com"));
        writeEnvironment(env, "target/allure-results/environment.properties");
    }
}
