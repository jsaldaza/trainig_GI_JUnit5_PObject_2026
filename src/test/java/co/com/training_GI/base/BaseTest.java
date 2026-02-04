package co.com.training_GI.base;

import co.com.training_GI.config.TestConfig;
import co.com.training_GI.driver.DriverFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import co.com.training_GI.evidence.EvidenceWatcher;

import java.time.Duration;

public abstract class BaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;

    @RegisterExtension
    final EvidenceWatcher evidenceWatcher = new EvidenceWatcher(() -> driver);

    @BeforeEach
    void setUp() {
        driver = DriverFactory.createDriver();
        if (TestConfig.maximize()) {
            driver.manage().window().maximize();
        }
        wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.timeoutSeconds()));
    }
}
