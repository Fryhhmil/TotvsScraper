package br.gov.pi.tce.totvsscraper.repository;

import br.gov.pi.tce.totvsscraper.model.RelacaoFalta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RelacaoFaltaRepository extends JpaRepository<RelacaoFalta, Integer> {

    RelacaoFalta findByCodigoDisciplina(String codigoDisciplina);
}
