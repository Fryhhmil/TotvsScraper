package br.gov.pi.tce.totvsscraper;

import br.gov.pi.tce.totvsscraper.service.FaltasScraperService;
import br.gov.pi.tce.totvsscraper.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TotvsScraperApplication {

    public static void main(String[] args) {
        SpringApplication.run(TotvsScraperApplication.class, args);
    }

//    @Override
//    public void run(String... args) throws Exception {
//        String cookie = loginService.acessarPortal("03524463312", "NINJA007");
//        faltasScraperService.acessarEExtrairFaltas(cookie);
//
//    }
}
