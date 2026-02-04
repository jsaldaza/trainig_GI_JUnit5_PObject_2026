package co.com.training_GI.driver;

import co.com.training_GI.config.TestConfig;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public final class DriverFactory {
    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        String browser = TestConfig.browser();
        if ("chrome".equalsIgnoreCase(browser)) {
            return createChrome();
        }
        throw new IllegalArgumentException("Unsupported browser: " + browser);
    }

    private static WebDriver createChrome() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-search-engine-choice-screen");
        if (TestConfig.headless()) {
            options.addArguments("--headless=new");
        }
        return new ChromeDriver(options);
    }
}
