package co.com.training_GI.tests;

import co.com.training_GI.base.BaseTest;
import co.com.training_GI.pages.WikipediaHomePage;
import co.com.training_GI.pages.WikipediaResultsPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class WikipediaSearchTest extends BaseTest {
    private static final String VALID_TERM = "JUnit 5";
    private static final String INVALID_TERM = "zzzxqyqweqweqweqweqwe12345";

    @Test
    @DisplayName("Busqueda valida muestra el articulo esperado")
    void shouldFindArticleBySearch() {
        WikipediaHomePage home = new WikipediaHomePage(driver, wait).open();
        WikipediaResultsPage results = home.searchFor(VALID_TERM)
                .openFirstResultIfPresent();
        String heading = results.getHeadingText();
        assertTrue(heading.toLowerCase().contains("junit"));
    }

    @Test
    @DisplayName("Busqueda invalida muestra mensaje de no resultados")
    void shouldShowNoResultsForUnknownTerm() {
        WikipediaHomePage home = new WikipediaHomePage(driver, wait).open();
        WikipediaResultsPage results = home.searchFor(INVALID_TERM);
        assertTrue(results.isNoResultsMessageVisible());
    }
}
