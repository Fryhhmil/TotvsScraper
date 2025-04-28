package br.gov.pi.tce.totvsscraper.dto;

import br.gov.pi.tce.totvsscraper.model.HorarioAluno;
import lombok.Data;

@Data
public class HorarioAlunoLinhaDTO {
    private HorarioAluno segunda;
    private HorarioAluno terca;
    private HorarioAluno quarta;
    private HorarioAluno quinta;
    private HorarioAluno sexta;
}
