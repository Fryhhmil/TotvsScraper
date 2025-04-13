package br.gov.pi.tce.totvsscraper.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class LoginService {

    private WebDriver driver;
    private static final String PORTAL_URL = "https://grupoeducacional127611.rm.cloudtotvs.com.br/FrameHTML/Web/app/edu/PortalEducacional/login/";


    @PostConstruct
    public void setup() {
        WebDriverManager.chromedriver()
                .browserVersionDetectionCommand("/usr/bin/chromium --version")
                .setup();
    }

    @PreDestroy
    public void cleanup() {
        if (driver != null) {
            driver.quit();
        }
    }

    public String acessarPortal(String usuario, String senha) {
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new"); // usa o modo headless moderno
            options.addArguments("--disable-gpu"); // evita alguns bugs gráficos mesmo no headless
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080"); // garante uma viewport decente
//            options.addArguments("--user-data-dir=/home/chrome-profile-" + UUID.randomUUID());

            driver = new ChromeDriver(options);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

            driver.get(PORTAL_URL);

            WebElement userField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("User")));
            WebElement passField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("Pass")));
            WebElement acessarBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@type='submit' and @value='Acessar']")));

            userField.sendKeys(usuario);
            passField.sendKeys(senha);
            acessarBtn.click();

//            // Espera o "Carregando" aparecer (opcional, mas ajuda em alguns casos)
//            wait.until(ExpectedConditions.presenceOfElementLocated(
//                    By.xpath("//p[contains(@class, 'text-muted') and contains(text(), 'Carregando')]")
//            ));
//
//            // Espera o "Carregando" desaparecer
//            wait.until(ExpectedConditions.invisibilityOfElementLocated(
//                    By.xpath("//p[contains(@class, 'text-muted') and contains(text(), 'Carregando')]")
//            ));

            wait.until(d -> {
                String currentUrl = d.getCurrentUrl();
                System.out.println("URL atual: " + currentUrl);
                return currentUrl.equals("https://grupoeducacional127611.rm.cloudtotvs.com.br/FrameHTML/Web//app/edu/PortalEducacional/#/");
            });


            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//p[contains(@class, 'text-muted') and contains(text(), 'Carregando')]")));


//            // Vai para a página de faltas
//            driver.get("https://grupoeducacional127611.rm.cloudtotvs.com.br/FrameHTML/Web//app/edu/PortalEducacional/#/faltas");
//
//            // Aguarda a tabela aparecer (se quiser garantir)
////            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table.grid")));
//            TimeUnit.SECONDS.sleep(5);

            // Pega os cookies
            Set<Cookie> cookies = driver.manage().getCookies();

            // Transforma em uma string no formato: nome1=valor1; nome2=valor2;
            String cookieHeader = cookies.stream()
                    .map(cookie -> cookie.getName() + "=" + cookie.getValue())
                    .collect(Collectors.joining("; "));

            System.out.println("Header de Cookies:");
            System.out.println(cookieHeader);

            return cookieHeader; // pode ser retornado ou usado numa próxima requisição
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro: " + e.getMessage();
        }
        finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
}