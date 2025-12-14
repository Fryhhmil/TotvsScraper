package br.gov.pi.tce.totvsscraper.service;

import br.gov.pi.tce.totvsscraper.dto.FaltaDTO;
import br.gov.pi.tce.totvsscraper.model.Falta;
import br.gov.pi.tce.totvsscraper.model.OrigemDTO;
import br.gov.pi.tce.totvsscraper.model.RelacaoFalta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class FaltasScraperService {
    @Autowired
    private RelacaoFaltaService relacaoFaltaService;

    public List<FaltaDTO> acessarEExtrairFaltas(String cokie) {
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
        ResponseEntity<OrigemDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                OrigemDTO.class
        );

        return this.toFaltaDTO(response.getBody().getData().getFaltasEtapa());
    }

    @Transactional(rollbackFor = Exception.class)
    public synchronized List<FaltaDTO> toFaltaDTO(List<Falta> faltas) {
        List<FaltaDTO> faltasDTO = new ArrayList<>();
        for (Falta falta : faltas) {
            FaltaDTO dto = new FaltaDTO();
            dto.setCodigoDisciplina(falta.getCodDisc());
            dto.setCodigoTurma(falta.getCodTurma());
            dto.setNomeMateria(falta.getDisciplina());
            dto.setPercentual(falta.getPercentual());

            try {
                dto.setFaltas(Integer.parseInt(falta.getTotalFaltas()) / 2);
            } catch (Exception e) {
                dto.setFaltas(0);
            }

            RelacaoFalta relacaoFaltas = relacaoFaltaService
                    .buscarPorCodigoDisciplina(dto.getCodigoDisciplina());

            Integer podeFaltar = null;
            if (relacaoFaltas == null) {
                RelacaoFalta novoRelacaoFalta = new RelacaoFalta(dto);
                this.relacaoFaltaService.salvar(novoRelacaoFalta);
            } else if (relacaoFaltas.getDiasParaFaltar() != null) {
                podeFaltar = relacaoFaltas.getDiasParaFaltar() - dto.getFaltas();
            }
            dto.setPodeFaltar(podeFaltar);

            faltasDTO.add(dto);

        }

        return faltasDTO;
    }
}
