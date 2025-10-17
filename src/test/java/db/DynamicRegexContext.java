package db;

import java.util.HashMap;
import java.util.Map;

/**
 * Scenario-scoped context for dynamically generated regex-based test data.
 * Uses ThreadLocal to isolate data when scenarios run in parallel.
 */
public class DynamicRegexContext {
    private static final ThreadLocal<Map<String, Object>> CONTEXT = ThreadLocal.withInitial(HashMap::new);

    public static void put(String key, Object value) { CONTEXT.get().put(key, value); }
    public static Object get(String key) { return CONTEXT.get().get(key); }
    public static String getString(String key) { Object v = get(key); return v == null ? null : v.toString(); }
    public static Integer getInt(String key) { Object v = get(key); return v instanceof Integer ? (Integer) v : null; }
    public static void clear() { CONTEXT.get().clear(); }

    // Convenience wrappers for regex generation and caching
    public static int generateInt(String key, String regex) {
        int val = RegexDataGenerator.generateInt(regex);
        put(key, val);
        return val;
    }
    public static String generateString(String key, String regex) {
        String val = RegexDataGenerator.generateFor(key, regex);
        put(key, val);
        return val;
    }
}

