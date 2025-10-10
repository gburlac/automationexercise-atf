package support;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertyReader {
    private static final Properties properties = new Properties();

    public static void loadProperties(String propFileName) {
        try (InputStream input = PropertyReader.class.getClassLoader().getResourceAsStream(propFileName)) {
            if (input == null) {
                log.error("Property file '{}' not found in the classpath", propFileName);
                return;
            }
            properties.load(input);
            log.info("Loaded properties from {}", propFileName);
        } catch (IOException ex) {
            log.error("Error loading properties file: {}", ex.getMessage());
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}

