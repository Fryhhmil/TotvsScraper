package br.gov.pi.tce.totvsscraper.service;

import br.gov.pi.tce.totvsscraper.dto.CookieTurmaDto;
import br.gov.pi.tce.totvsscraper.model.HorarioAluno;
import br.gov.pi.tce.totvsscraper.model.OrigemDTO;
import br.gov.pi.tce.totvsscraper.repository.HorarioAlunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class HorarioAlunoService {
    @Autowired
    private HorarioAlunoRepository horarioAlunoRepository;

    protected List<HorarioAluno> acessarEExtrairHorarioAluno(String cookie) {
        String url = "https://grupoeducacional127611.rm.cloudtotvs.com.br/FrameHTML/RM/API/TOTVSEducacional/QuadroHorarioAluno";

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

        // Coloca o cookie completo
        headers.set("Cookie", cookie);

        // Monta a entidade
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Faz a requisição
        ResponseEntity<OrigemDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                OrigemDTO.class
        );

        // Processa e retorna como quiser

        return Objects.requireNonNull(response.getBody()).getData().getHorarioAluno();
    }

    public List<HorarioAluno> getHorarioAluno(CookieTurmaDto cookie) {
        String codTurma = cookie.getCodTurma();
        List<HorarioAluno> horarioAluno = new ArrayList<>();

        if (cookie.getCookie() == null || cookie.getCookie().isEmpty()) throw  new RuntimeException("Cookie is null or empty");

        if (codTurma != null && !codTurma.isEmpty()) {
            horarioAluno = horarioAlunoRepository.findHorarioAlunoByCodTurma(codTurma);
        }

        if (horarioAluno.isEmpty()) {
            horarioAluno =  this.acessarEExtrairHorarioAluno(cookie.getCookie());
            horarioAluno = horarioAluno.stream()
                    .filter(x -> x.getId() != null)
                    .toList();
        }

        return horarioAluno;
    }

}
