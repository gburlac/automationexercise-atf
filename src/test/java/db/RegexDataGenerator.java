package db;

import com.mifmif.common.regex.Generex;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class RegexDataGenerator {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    // Thread-local cache for scenario specific values (key -> generated value)
    private static final ThreadLocal<Map<String, String>> TL_CACHE = ThreadLocal.withInitial(HashMap::new);

    /**
     * Primary generation using Generex. Falls back to manual simple parser if Generex fails.
     */
    public static String generate(String regex) {
        try {
            Generex generex = new Generex(regex);
            return generex.random();
        } catch (Throwable t) { // NoClassDefFoundError or Pattern issues
            return fallbackGenerate(regex);
        }
    }

    /** Simple numeric generation returning int parsed from regex, falling back to random range when needed. */
    public static int generateInt(String regex) {
        String value = generate(regex);
        if (!value.matches("\\d+")) {
            // Try to extract a quantifier {n} or {min,max}
            int len = 5;
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\\\d\\{(\\d+)(?:,(\\d+))?}"
            ).matcher(regex);
            if (m.find()) {
                int a = Integer.parseInt(m.group(1));
                String bStr = m.group(2);
                int b = bStr != null ? Integer.parseInt(bStr) : a;
                len = a == b ? a : ThreadLocalRandom.current().nextInt(a, b + 1);
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len; i++) sb.append(ThreadLocalRandom.current().nextInt(0, 10));
            value = sb.toString();
        }
        // Guard against overflow
        try { return Integer.parseInt(value); } catch (NumberFormatException e) { return ThreadLocalRandom.current().nextInt(10000, 99999); }
    }

    /** Generate and cache by key so subsequent calls within same scenario return identical value. */
    public static String generateAndCache(String key, String regex) {
        Map<String, String> cache = TL_CACHE.get();
        return cache.computeIfAbsent(key, k -> generate(regex));
    }
    public static String generateFor(String key, String regex) {
        return generateAndCache(key, regex);
    }

    /** Reset cache at end of scenario. */
    public static void clearCache() { TL_CACHE.get().clear(); }

    /** Generate a unique list of values for a regex (best effort). */
    public static List<String> generateUniqueList(String regex, int count) {
        Set<String> set = new LinkedHashSet<>();
        int attempts = 0;
        while (set.size() < count && attempts < count * 10) { // avoid infinite loop
            set.add(generate(regex));
            attempts++;
        }
        return new ArrayList<>(set);
    }

    /** Generate list (may contain duplicates) */
    public static List<String> generateList(String regex, int count) {
        List<String> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) list.add(generate(regex));
        return list;
    }

    /** Template expansion: replace {{key}} tokens with generated values based on regex map. */
    public static String generateTemplate(String template, Map<String, String> regexByKey) {
        String result = template;
        for (Map.Entry<String, String> e : regexByKey.entrySet()) {
            String token = "{{" + e.getKey() + "}}";
            if (result.contains(token)) {
                String value = generateAndCache(e.getKey(), e.getValue());
                result = result.replace(token, value);
            }
        }
        return result;
    }

    /** Basic fallback generator for common simple patterns when Generex is unavailable. */
    private static String fallbackGenerate(String regex) {
        // Handle repetitive groups like \\d{5}
        java.util.regex.Matcher quantifierMatcher = java.util.regex.Pattern.compile("^(\\\\[A-Za-z0-9-]+\\]|\\\\d|[A-Za-z])\\{(\\d+)(?:,(\\d+))?}+$").matcher(regex);
        if (quantifierMatcher.find()) {
            String base = quantifierMatcher.group(1);
            int min = Integer.parseInt(quantifierMatcher.group(2));
            String maxStr = quantifierMatcher.group(3);
            int max = maxStr != null ? Integer.parseInt(maxStr) : min;
            int len = min == max ? min : ThreadLocalRandom.current().nextInt(min, max + 1);
            return repeatBase(base, len);
        }
        // Simple patterns like [A-Z]{3}[a-z]{2}\d{4}
        StringBuilder sb = new StringBuilder();
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\[A-Z]-?A-Z]?|\\[A-Z]{1}\\]|\\[A-Z]{1}\\)|\\[A-Z]{1}|\\[A-Z]{1}|\\[A-Z]{1}|\\[A-Z]{1}|\\[A-Z]{1}").matcher("");
        // Manual mini parser
        for (int i = 0; i < regex.length();) {
            char c = regex.charAt(i);
            if (c == '[') { // character class
                int end = regex.indexOf(']', i);
                String clazz = regex.substring(i + 1, end); // e.g. A-Z or a-z
                i = end + 1;
                // Quantifier?
                int quant = 1;
                if (i < regex.length() && regex.charAt(i) == '{') {
                    int qEnd = regex.indexOf('}', i);
                    String qBody = regex.substring(i + 1, qEnd);
                    String[] parts = qBody.split(",");
                    int min = Integer.parseInt(parts[0]);
                    int max = parts.length > 1 ? Integer.parseInt(parts[1]) : min;
                    quant = min == max ? min : ThreadLocalRandom.current().nextInt(min, max + 1);
                    i = qEnd + 1;
                }
                for (int k = 0; k < quant; k++) sb.append(randomCharFromClass(clazz));
            } else if (c == '\\') { // escape sequence like \d
                if (i + 1 < regex.length()) {
                    char type = regex.charAt(i + 1);
                    i += 2;
                    int quant = 1;
                    if (i < regex.length() && regex.charAt(i) == '{') {
                        int qEnd = regex.indexOf('}', i);
                        String qBody = regex.substring(i + 1, qEnd);
                        String[] parts = qBody.split(",");
                        int min = Integer.parseInt(parts[0]);
                        int max = parts.length > 1 ? Integer.parseInt(parts[1]) : min;
                        quant = min == max ? min : ThreadLocalRandom.current().nextInt(min, max + 1);
                        i = qEnd + 1;
                    }
                    for (int k = 0; k < quant; k++) sb.append(type == 'd' ? SECURE_RANDOM.nextInt(10) : type);
                }
            } else {
                // Literal char (optionally followed by quantifier)
                i++;
                int quant = 1;
                if (i < regex.length() && regex.charAt(i) == '{') {
                    int qEnd = regex.indexOf('}', i);
                    String qBody = regex.substring(i + 1, qEnd);
                    String[] parts = qBody.split(",");
                    int min = Integer.parseInt(parts[0]);
                    int max = parts.length > 1 ? Integer.parseInt(parts[1]) : min;
                    quant = min == max ? min : ThreadLocalRandom.current().nextInt(min, max + 1);
                    i = qEnd + 1;
                }
                for (int k = 0; k < quant; k++) sb.append(c);
            }
        }
        String out = sb.toString();
        if (out.isEmpty()) {
            // Final fallback random string
            for (int i2 = 0; i2 < 8; i2++) sb.append((char) ('A' + SECURE_RANDOM.nextInt(26)));
            out = sb.toString();
        }
        return out;
    }

    private static String repeatBase(String base, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append(resolveBase(base));
        return sb.toString();
    }

    private static String resolveBase(String base) {
        if ("\\d".equals(base)) return String.valueOf(ThreadLocalRandom.current().nextInt(10));
        if (base.matches("[A-Z]-?Z?")) return String.valueOf((char) ('A' + SECURE_RANDOM.nextInt(26)));
        if (base.matches("[a-z]-?z?")) return String.valueOf((char) ('a' + SECURE_RANDOM.nextInt(26)));
        return base.replace("\\", "");
    }

    private static char randomCharFromClass(String clazz) {
        // Supports A-Z, a-z, 0-9 simple ranges
        if (clazz.equals("A-Z")) return (char) ('A' + SECURE_RANDOM.nextInt(26));
        if (clazz.equals("a-z")) return (char) ('a' + SECURE_RANDOM.nextInt(26));
        if (clazz.equals("0-9")) return (char) ('0' + SECURE_RANDOM.nextInt(10));
        // If explicit list of chars
        char[] arr = clazz.toCharArray();
        return arr[SECURE_RANDOM.nextInt(arr.length)];
    }
}
