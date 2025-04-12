package br.gov.pi.tce.totvsscraper.service;

import br.gov.pi.tce.totvsscraper.model.FaltaData;
import br.gov.pi.tce.totvsscraper.model.Faltas;
import ch.qos.logback.core.util.TimeUtil;
import jakarta.annotation.PostConstruct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class FaltasScraperService {

    public void acessarEExtrairFaltas(String cokie) {
        String url = "https://grupoeducacional127611.rm.cloudtotvs.com.br/FrameHTML/RM/API/TOTVSEducacional/FaltaEtapa";

        // Cria o RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Cria os headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json, text/plain, */*");
        headers.set("Accept-Language", "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7");
        headers.set("Priority", "u=1, i");
        headers.set("Referer", "https://grupoeducacional127611.rm.cloudtotvs.com.br/FrameHTML/Web//app/edu/PortalEducacional/");
        headers.set("Sec-CH-UA", "\"Chromium\";v=\"134\", \"Not:A-Brand\";v=\"24\", \"Google Chrome\";v=\"134\"");
        headers.set("Sec-CH-UA-Mobile", "?0");
        headers.set("Sec-CH-UA-Platform", "\"Windows\"");
        headers.set("Sec-Fetch-Dest", "empty");
        headers.set("Sec-Fetch-Mode", "cors");
        headers.set("Sec-Fetch-Site", "same-origin");
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36");

        // Coloque seu cookie completo aqui
        headers.set("Cookie", cokie);

        // Monta a entidade com os headers
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Faz a requisição
        ResponseEntity<Faltas> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Faltas.class
        );

        // Imprime a resposta
        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Body: " + response.getBody());
    }
}
