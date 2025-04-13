package br.gov.pi.tce.totvsscraper.service;

import br.gov.pi.tce.totvsscraper.model.RelacaoFalta;
import br.gov.pi.tce.totvsscraper.repository.RelacaoFaltaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelacaoFaltaService {
    @Autowired
    private RelacaoFaltaRepository relacaoFaltaRepository;

    public RelacaoFalta buscarPorCodigoDisciplina(String codigoDisciplina) {
        return relacaoFaltaRepository.findByCodigoDisciplina(codigoDisciplina);
    }

    public void salvar(RelacaoFalta relacaoFalta) {
        relacaoFaltaRepository.save(relacaoFalta);
    }
}
