package co.com.training_GI.evidence;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class EvidenceManager {
    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private EvidenceManager() {
    }

    public static void capture(String status, String testName, WebDriver driver, Throwable error) {
        if (driver == null) {
            return;
        }

        String safeName = sanitize(testName);
        String timestamp = LocalDateTime.now().format(TS_FORMAT);
        Path dir = Paths.get("build", "evidences", status);

        try {
            Files.createDirectories(dir);
        } catch (IOException ignored) {
            return;
        }

        if (driver instanceof TakesScreenshot) {
            try {
                byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment("Screenshot - " + safeName, "image/png", new ByteArrayInputStream(bytes), ".png");
                File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                Path target = dir.resolve(safeName + "_" + timestamp + ".png");
                Files.copy(src.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception ignored) {
                // Avoid failing the test because evidence could not be captured.
            }
        }

        if (error != null) {
            Path errorFile = dir.resolve(safeName + "_" + timestamp + "_error.txt");
            try {
                String errorText = formatError(error);
                Allure.addAttachment("Error - " + safeName, "text/plain", errorText);
                Files.writeString(errorFile, errorText, StandardCharsets.UTF_8);
            } catch (IOException ignored) {
                // Ignore write failures to keep test outcome intact.
            }
        }
    }

    private static String sanitize(String name) {
        if (name == null || name.isBlank()) {
            return "test";
        }
        return name.replaceAll("[^a-zA-Z0-9._-]+", "_");
    }

    private static String formatError(Throwable error) {
        StringBuilder sb = new StringBuilder();
        sb.append(error).append(System.lineSeparator());
        for (StackTraceElement element : error.getStackTrace()) {
            sb.append("  at ").append(element).append(System.lineSeparator());
        }
        return sb.toString();
    }
}
