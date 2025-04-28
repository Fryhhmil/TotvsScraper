package br.gov.pi.tce.totvsscraper.controller;

import br.gov.pi.tce.totvsscraper.dto.CookieTurmaDto;
import br.gov.pi.tce.totvsscraper.dto.FaltaDTO;
import br.gov.pi.tce.totvsscraper.dto.HorarioAlunoDTO;
import br.gov.pi.tce.totvsscraper.dto.LoginFormDTO;
import br.gov.pi.tce.totvsscraper.exception.CookieException;
import br.gov.pi.tce.totvsscraper.model.HorarioAluno;
import br.gov.pi.tce.totvsscraper.service.FaltasScraperService;
import br.gov.pi.tce.totvsscraper.service.HorarioAlunoService;
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
    @Autowired
    private HorarioAlunoService horarioAlunoService;

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

    @PostMapping(value = "/buscar-horario")
    public ResponseEntity<?> buscarHorario(
            @RequestBody String cookie,
            @RequestParam(required = false) String codTurma
    ) {
        try {
            if (cookie == null || cookie.isBlank()) {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
            HorarioAlunoDTO horarioAluno = horarioAlunoService.getHorarioAluno(new CookieTurmaDto(codTurma, cookie));
            if (horarioAluno != null) {
                return ResponseEntity.status(HttpStatus.OK).body(horarioAluno);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        catch (CookieException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
