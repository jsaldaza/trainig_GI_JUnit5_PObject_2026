package co.com.training_GI.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class TestConfig {
    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPS = load();

    private TestConfig() {
    }

    public static String baseUrl() {
        return get("baseUrl", "https://en.wikipedia.org/");
    }

    public static String browser() {
        return get("browser", "chrome");
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("headless", "false"));
    }

    public static boolean maximize() {
        return Boolean.parseBoolean(get("maximize", "true"));
    }

    public static long timeoutSeconds() {
        return Long.parseLong(get("timeoutSeconds", "10"));
    }

    private static String get(String key, String defaultValue) {
        String override = System.getProperty(key);
        if (override != null && !override.isBlank()) {
            return override;
        }
        return PROPS.getProperty(key, defaultValue);
    }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream input = TestConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException ignored) {
        }
        return properties;
    }
}
