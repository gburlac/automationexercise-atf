package db;

import java.io.InputStream;
import java.util.Properties;

public class TestDataGenerators {
    private static final Properties PROPS = new Properties();
    static {
        try (InputStream is = TestDataGenerators.class.getClassLoader().getResourceAsStream("regex-data.properties")) {
            if (is == null) {
                throw new IllegalStateException("regex-data.properties not found on classpath");
            }
            PROPS.load(is);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load regex-data.properties", e);
        }
    }

    // Concrete generator methods (names referenced in feature file)
    public static int employeeId() { return RegexDataGenerator.generateInt(PROPS.getProperty("employeeId")); }
    public static String employeeName() { return RegexDataGenerator.generate(PROPS.getProperty("employeeName")); }
    public static String updatedEmployeeName() { return RegexDataGenerator.generate(PROPS.getProperty("updatedEmployeeName")); }
    public static int employeeIdAlt() { return RegexDataGenerator.generateInt(PROPS.getProperty("employeeIdAlt")); }
    public static String employeeNameAlt() { return RegexDataGenerator.generate(PROPS.getProperty("employeeNameAlt")); }
    public static String updatedEmployeeNameAlt() { return RegexDataGenerator.generate(PROPS.getProperty("updatedEmployeeNameAlt")); }
    public static String employeeSurname() { return RegexDataGenerator.generate(PROPS.getProperty("employeeSurname")); }

    // Generic reflection-based invokers
    public static int invokeInt(String methodName) {
        try {
            return (Integer) TestDataGenerators.class.getMethod(methodName).invoke(null);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("No int generator method found: " + methodName, e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke int generator method: " + methodName, e);
        }
    }
    public static String invokeString(String methodName) {
        try {
            return (String) TestDataGenerators.class.getMethod(methodName).invoke(null);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("No string generator method found: " + methodName, e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke string generator method: " + methodName, e);
        }
    }
}
