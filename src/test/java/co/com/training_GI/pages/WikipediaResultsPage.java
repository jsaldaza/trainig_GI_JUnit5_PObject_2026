package co.com.training_GI.pages;

import co.com.training_GI.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WikipediaResultsPage extends BasePage {
    private final By heading = By.id("firstHeading");
    private final By searchResults = By.cssSelector(".mw-search-results li a");
    private final By searchInfo = By.cssSelector(".mw-search-results-info");
    private final By noResultsMessage = By.cssSelector(".mw-search-nonefound");

    public WikipediaResultsPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public String getHeadingText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(heading)).getText();
    }

    public boolean hasSearchResults() {
        return !driver.findElements(searchResults).isEmpty();
    }

    public WikipediaResultsPage openFirstResult() {
        wait.until(ExpectedConditions.elementToBeClickable(searchResults)).click();
        return new WikipediaResultsPage(driver, wait);
    }

    public WikipediaResultsPage openFirstResultIfPresent() {
        if (hasSearchResults()) {
            return openFirstResult();
        }
        return this;
    }

    public boolean isNoResultsMessageVisible() {
        String info = getSearchInfoText().toLowerCase();
        if (!info.isBlank() && (info.contains("no results")
                || info.contains("no result")
                || info.contains("no se encontraron")
                || info.contains("sin resultados"))) {
            return true;
        }
        if (!driver.findElements(noResultsMessage).isEmpty()) {
            return true;
        }
        return isSearchResultsHeading() && !hasSearchResults();
    }

    public String getSearchInfoText() {
        if (driver.findElements(searchInfo).isEmpty()) {
            return "";
        }
        return driver.findElement(searchInfo).getText();
    }

    private boolean isSearchResultsHeading() {
        String headingText = getHeadingText().toLowerCase();
        return headingText.contains("search results") || headingText.contains("resultados");
    }
}
