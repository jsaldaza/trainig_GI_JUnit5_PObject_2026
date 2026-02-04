package co.com.training_GI.evidence;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.WebDriver;

import java.util.function.Supplier;

public final class EvidenceWatcher implements TestWatcher {
    private final Supplier<WebDriver> driverSupplier;

    public EvidenceWatcher(Supplier<WebDriver> driverSupplier) {
        this.driverSupplier = driverSupplier;
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        captureAndQuit("passed", context, null);
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        captureAndQuit("failed", context, cause);
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        captureAndQuit("aborted", context, cause);
    }

    private void captureAndQuit(String status, ExtensionContext context, Throwable error) {
        WebDriver driver = driverSupplier.get();
        if (driver == null) {
            return;
        }
        try {
            EvidenceManager.capture(status, context.getDisplayName(), driver, error);
        } finally {
            try {
                driver.quit();
            } catch (Exception ignored) {
            }
        }
    }
}
