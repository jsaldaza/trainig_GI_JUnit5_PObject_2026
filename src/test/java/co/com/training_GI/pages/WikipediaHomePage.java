package co.com.training_GI.pages;

import co.com.training_GI.base.BasePage;
import co.com.training_GI.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WikipediaHomePage extends BasePage {
    private final By searchInput = By.name("search");

    public WikipediaHomePage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public WikipediaHomePage open() {
        driver.get(TestConfig.baseUrl());
        wait.until(ExpectedConditions.elementToBeClickable(searchInput));
        return this;
    }

    public WikipediaResultsPage searchFor(String query) {
        var input = wait.until(ExpectedConditions.elementToBeClickable(searchInput));
        input.clear();
        input.sendKeys(query, Keys.ENTER);
        return new WikipediaResultsPage(driver, wait);
    }
}
