package br.gov.pi.tce.totvsscraper.repository;

import br.gov.pi.tce.totvsscraper.model.HorarioAluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorarioAlunoRepository extends JpaRepository<HorarioAluno, Long> {

    List<HorarioAluno> findHorarioAlunoByCodTurma(String codTurma);
}