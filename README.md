# Manual de Automatizacion con JUnit 5 + Selenium (Page Object)

Este proyecto es un laboratorio didactico para aprender automatizacion UI con **JUnit 5** y **Selenium** usando **Page Object Model (POM)**. Se elimino Screenplay y pruebas unitarias para enfocarnos en una sola forma de trabajo y entenderla a profundidad.

## Objetivo
- Aprender la estructura minima de un proyecto de automatizacion con JUnit 5.
- Entender como separar responsabilidades: configuracion, driver, paginas y pruebas.
- Practicar un flujo real de automatizacion en Wikipedia con Page Object.

## Requisitos
- Java 17 instalado.
- Chrome instalado.
- Gradle Wrapper incluido en el proyecto.
- Acceso a internet para que Selenium Manager descargue el driver de Chrome en la primera ejecucion.

## Como ejecutar las pruebas
En Windows:
```
.\\gradlew.bat test
```
En Linux o macOS:
```
./gradlew test
```

Si Gradle dice `up-to-date` y no se abre el navegador, fuerza la ejecucion:
```
.\gradlew.bat test --rerun-tasks
```
Usalo cuando quieras que los tests se ejecuten siempre, aunque no haya cambios.

Para ver evidencias en el navegador con Allure:
```
allure serve build/allure-results
```

## Reportes y evidencias
Reportes HTML:
- `build/reports/tests/test/index.html`

Evidencias (screenshots):
- Exitos: `build/evidences/passed/`
- Fallos: `build/evidences/failed/`

Cuando un test falla, se genera un archivo `*_error.txt` con la descripcion del error y el stack trace.

Reporte Allure (con screenshots embebidos):
1) Ejecuta las pruebas (ej: `.\gradlew.bat test --rerun-tasks`)
2) Asegurate de tener instalado **Allure CLI**
3) Genera y abre el reporte:
```
allure serve build/allure-results
```
Si prefieres un reporte estatico:
```
allure generate build/allure-results -o build/allure-report --clean
```

## Estructura del proyecto (solo Page Object)
```
trainig_GI_JUnit5/
|-- build.gradle
|-- settings.gradle
|-- src/
|   |-- test/
|   |   |-- java/
|   |   |   |-- co/com/training_GI/
|   |   |   |   |-- base/
|   |   |   |   |   |-- BasePage.java
|   |   |   |   |   |-- BaseTest.java
|   |   |   |   |-- config/
|   |   |   |   |   |-- TestConfig.java
|   |   |   |   |-- driver/
|   |   |   |   |   |-- DriverFactory.java
|   |   |   |   |-- evidence/
|   |   |   |   |   |-- EvidenceManager.java
|   |   |   |   |   |-- EvidenceWatcher.java
|   |   |   |   |-- pages/
|   |   |   |   |   |-- WikipediaHomePage.java
|   |   |   |   |   |-- WikipediaResultsPage.java
|   |   |   |   |-- tests/
|   |   |   |   |   |-- WikipediaSearchTest.java
|   |   |-- resources/
|   |   |   |-- config.properties
```

Nota: `src/main/java` esta vacio a proposito. Todo el codigo es de pruebas.

---

# Codigo y explicacion (archivo por archivo)

## 1) build.gradle
**Que hace:** define el proyecto, dependencias y configuracion de JUnit 5.

```gradle
plugins {
    id 'java'
}

group = 'co.com.training_GI'
version = '1.0-SNAPSHOT'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation platform('org.junit:junit-bom:5.14.2')
    testImplementation 'org.junit.jupiter:junit-jupiter'
    testImplementation 'org.seleniumhq.selenium:selenium-java:4.40.0'
    testImplementation 'io.qameta.allure:allure-junit5:2.25.0'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}

test {
    useJUnitPlatform()
    testLogging {
        events "passed", "skipped", "failed"
    }
    systemProperty "allure.results.directory", "$buildDir/allure-results"
}
```

**Como funciona:**
- Usa Java 17 con toolchain.
- JUnit 5 y Selenium estan solo en el scope de pruebas.
- `useJUnitPlatform()` activa el motor de JUnit 5.

---

## 2) settings.gradle
**Que hace:** define el nombre del proyecto para Gradle.

```gradle
rootProject.name = 'trainig_GI_JUnit5'
```

---

## 3) src/test/resources/config.properties
**Que hace:** parametros simples para ejecutar la prueba sin tocar el codigo.

```properties
baseUrl=https://en.wikipedia.org/
browser=chrome
headless=false
timeoutSeconds=10
maximize=true
```

**Como funciona:**
- Puedes cambiar `headless=true` para ejecutar sin abrir ventana.
- `timeoutSeconds` controla el tiempo de espera de los waits.

---

## 4) src/test/java/co/com/training_GI/base/BasePage.java
**Que hace:** clase base para todas las paginas (Page Objects).

```java
package co.com.training_GI.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }
}
```

**Como funciona:**
- Todas las paginas heredan `driver` y `wait`.
- Evita repetir codigo en cada Page Object.

---

## 5) src/test/java/co/com/training_GI/base/BaseTest.java
**Que hace:** prepara el navegador y registra el capturador de evidencias.

```java
package co.com.training_GI.base;

import co.com.training_GI.config.TestConfig;
import co.com.training_GI.driver.DriverFactory;
import co.com.training_GI.evidence.EvidenceWatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

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
```

**Como funciona:**
- `@BeforeEach` crea el navegador y el WebDriverWait.
- `EvidenceWatcher` captura evidencia y luego cierra el navegador.

---

## 6) src/test/java/co/com/training_GI/config/TestConfig.java
**Que hace:** lee `config.properties` y permite override por System Properties.

```java
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
```

**Como funciona:**
- Si pasas `-Dheadless=true`, esa propiedad tiene prioridad sobre el archivo.
- Si no hay archivo, usa valores por defecto.

---

## 7) src/test/java/co/com/training_GI/driver/DriverFactory.java
**Que hace:** crea el WebDriver segun el navegador configurado.

```java
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
```

**Como funciona:**
- Por ahora solo soporta Chrome para mantener el proyecto simple.
- Si activas `headless=true`, el navegador se ejecuta sin UI.

---

## 8) src/test/java/co/com/training_GI/evidence/EvidenceManager.java
**Que hace:** guarda evidencias (screenshots) y, si hay fallo, un archivo con el error.

```java
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
            }
        }

        if (error != null) {
            Path errorFile = dir.resolve(safeName + "_" + timestamp + "_error.txt");
            try {
                String errorText = formatError(error);
                Allure.addAttachment("Error - " + safeName, "text/plain", errorText);
                Files.writeString(errorFile, errorText, StandardCharsets.UTF_8);
            } catch (IOException ignored) {
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
```

**Como funciona:**
- Guarda el screenshot en `build/evidences/<estado>/`.
- Adjunta el screenshot y el error al reporte Allure.
- Si hay error, crea `*_error.txt` con la descripcion.

---

## 9) src/test/java/co/com/training_GI/evidence/EvidenceWatcher.java
**Que hace:** se engancha al ciclo de JUnit 5 para capturar evidencia en exito o fallo.

```java
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
```

**Como funciona:**
- JUnit 5 llama este watcher al final de cada test.
- Captura evidencia y luego cierra el navegador.

---

## 10) src/test/java/co/com/training_GI/pages/WikipediaHomePage.java
**Que hace:** representa la pagina principal de Wikipedia.

```java
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
```

**Como funciona:**
- `open()` abre la pagina y espera a que el campo este listo.
- `searchFor()` escribe el texto y presiona ENTER.

---

## 11) src/test/java/co/com/training_GI/pages/WikipediaResultsPage.java
**Que hace:** representa la pagina de resultados y el articulo abierto.

```java
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
```

**Como funciona:**
- `hasSearchResults()` permite decidir si hay lista de resultados.
- `openFirstResult()` abre el primer resultado cuando existe.
- `openFirstResultIfPresent()` evita ifs repetidos en los tests.
- `isNoResultsMessageVisible()` valida el caso negativo.
- `getHeadingText()` lee el titulo del articulo.

---

## 12) src/test/java/co/com/training_GI/tests/WikipediaSearchTest.java
**Que hace:** prueba final usando Page Object y asserts de JUnit 5.

```java
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
```

**Como funciona:**
- Prueba positiva: abre Wikipedia, busca un termino y valida el titulo del articulo.
- Prueba negativa: busca un termino inexistente y valida el mensaje de no resultados.

---

# Siguiente paso sugerido
Cuando domines este flujo, puedes:
- Agregar mas Page Objects para otros flujos.
- Parametrizar mas pruebas con `@ParameterizedTest`.
- Integrar reportes (por ejemplo, Allure) sin cambiar la base de Page Object.
