package br.gov.pi.tce.totvsscraper.service;

import br.gov.pi.tce.totvsscraper.dto.CookieTurmaDto;
import br.gov.pi.tce.totvsscraper.dto.FaltaDTO;
import br.gov.pi.tce.totvsscraper.dto.HorarioAlunoDTO;
import br.gov.pi.tce.totvsscraper.dto.HorarioAlunoLinhaDTO;
import br.gov.pi.tce.totvsscraper.exception.CookieException;
import br.gov.pi.tce.totvsscraper.model.HorarioAluno;
import br.gov.pi.tce.totvsscraper.model.OrigemDTO;
import br.gov.pi.tce.totvsscraper.repository.HorarioAlunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class HorarioAlunoService {
    @Autowired
    private HorarioAlunoRepository horarioAlunoRepository;
    @Autowired
    private FaltasScraperService faltasScraperService;

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

    public HorarioAlunoDTO getHorarioAluno(CookieTurmaDto cookie) {
        HorarioAlunoDTO horarioAlunoDTO = new HorarioAlunoDTO();
        String codTurma = cookie.getCodTurma();
        List<HorarioAluno> horarioAluno = new ArrayList<>();

        if (cookie.getCookie() == null || cookie.getCookie().isEmpty()) throw  new RuntimeException("Cookie is null or empty");

        if (codTurma != null && !codTurma.isEmpty()) {
            horarioAluno = horarioAlunoRepository.findHorarioAlunoByCodTurma(codTurma);
        }

        if (horarioAluno.isEmpty()) {
            try {
                horarioAluno =  this.acessarEExtrairHorarioAluno(cookie.getCookie());
            }
            catch (HttpClientErrorException.Unauthorized e) {
                throw new CookieException("Cookie Expirado!");
            }

            horarioAluno = horarioAluno.stream()
                    .filter(x -> x.getId() != null)
                    .toList();
        }

        // Agora separar por dia
        List<HorarioAluno> segunda = new ArrayList<>();
        List<HorarioAluno> terca = new ArrayList<>();
        List<HorarioAluno> quarta = new ArrayList<>();
        List<HorarioAluno> quinta = new ArrayList<>();
        List<HorarioAluno> sexta = new ArrayList<>();

        for (HorarioAluno ha : horarioAluno) {
            switch (ha.getDiaSemana()) {
                case "2" -> segunda.add(ha);
                case "3" -> terca.add(ha);
                case "4" -> quarta.add(ha);
                case "5" -> quinta.add(ha);
                case "6" -> sexta.add(ha);
            }
        }

        // Definir o máximo de linhas que vamos precisar
        int max = Math.max(
                segunda.size(),
                Math.max(terca.size(), Math.max(quarta.size(), Math.max(quinta.size(), sexta.size())))
        );

        for (int i = 0; i < max; i++) {
            HorarioAlunoLinhaDTO linha = new HorarioAlunoLinhaDTO();
            linha.setSegunda(i < segunda.size() ? segunda.get(i) : null);
            linha.setTerca(i < terca.size() ? terca.get(i) : null);
            linha.setQuarta(i < quarta.size() ? quarta.get(i) : null);
            linha.setQuinta(i < quinta.size() ? quinta.get(i) : null);
            linha.setSexta(i < sexta.size() ? sexta.get(i) : null);

            horarioAlunoDTO.getLinhas().add(linha);
        }

        List<FaltaDTO> faltasDTO = faltasScraperService.acessarEExtrairFaltas(cookie.getCookie());

        horarioAlunoDTO.setPodefaltarSegunda(this.pegarMaiorValorHorarioLista(segunda, faltasDTO));
        horarioAlunoDTO.setPodefaltarTerca(this.pegarMaiorValorHorarioLista(terca, faltasDTO));
        horarioAlunoDTO.setPodefaltarQuarta(this.pegarMaiorValorHorarioLista(quarta, faltasDTO));
        horarioAlunoDTO.setPodefaltarQuinta(this.pegarMaiorValorHorarioLista(quinta, faltasDTO));
        horarioAlunoDTO.setPodefaltarSexta(this.pegarMaiorValorHorarioLista(sexta, faltasDTO));

        return horarioAlunoDTO;
    }

    private int pegarMaiorValorHorarioLista(List<HorarioAluno> lista, List<FaltaDTO> faltasDTO) {
        Integer menorFalta = null;
        List<FaltaDTO> faltasSemana = new ArrayList<>();

        List<String> materiaSemana = lista.stream()
                .map(HorarioAluno::getCodDisc)
                .toList();

        for (String materia : materiaSemana) {
            List<FaltaDTO> faltaMateriaAtual = faltasDTO.stream()
                    .filter(faltaDTO -> faltaDTO.getCodigoDisciplina().equals(materia))
                    .toList();

            faltasSemana.addAll(faltaMateriaAtual);
        }

        for (FaltaDTO faltaDTO : faltasSemana) {
            if (menorFalta == null) {
                menorFalta = faltaDTO.getPodeFaltar();
                continue;
            }

            menorFalta = Math.min(menorFalta, faltaDTO.getPodeFaltar());
        }
        if (menorFalta == null) {
            throw new RuntimeException("Erro inesperado na menor falta");
        }

        return menorFalta;
    }

}
