package support;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertyReader {
    private static final Properties properties = new Properties();

    public static void loadPropertiesFromFile(String filePath) {
        try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
            log.info("Loaded properties from file: {}", filePath);
        } catch (IOException ex) {
            log.error("Error loading properties file '{}': {}", filePath, ex.getMessage());
        }
    }

    public static void loadTestProperties() {
        loadPropertiesFromFile("src/test/resources/config/test.properties");
    }

    /**
     * Gets a property value from the loaded properties.
     * Usage: String value = PropertyReader.getTestProperty("someKey");
     */
    public static String getTestProperty(String key) {
        return getProperty(key);
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
