package br.gov.pi.tce.totvsscraper.controller;

import br.gov.pi.tce.totvsscraper.dto.FaltaDTO;
import br.gov.pi.tce.totvsscraper.dto.LoginFormDTO;
import br.gov.pi.tce.totvsscraper.model.Falta;
import br.gov.pi.tce.totvsscraper.model.Faltas;
import br.gov.pi.tce.totvsscraper.service.FaltasScraperService;
import br.gov.pi.tce.totvsscraper.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@Slf4j
@RestController
@RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
public class HomeController {
    @Autowired
    private LoginService loginService;
    @Autowired
    private FaltasScraperService faltasScraperService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginFormDTO loginForm) {
        try {
            if (loginForm.getCpf().isBlank() || loginForm.getCpf().isBlank()) {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
            String cookie = loginService.acessarPortal(loginForm.getCpf(), loginForm.getSenha());
            if (cookie != null && !cookie.toLowerCase().contains("erro")) {
                return ResponseEntity.status(HttpStatus.OK).body(cookie);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    @PostMapping("/buscar-faltas")
    public ResponseEntity<?> buscarFaltas(@RequestBody String cookie) {
        try {
            if (cookie == null || cookie.isBlank()) {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
            List<FaltaDTO> faltas = faltasScraperService.acessarEExtrairFaltas(cookie);
            if (faltas != null && !faltas.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(faltas);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }
}
