package br.gov.pi.tce.totvsscraper.dto;

import br.gov.pi.tce.totvsscraper.model.HorarioAluno;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HorarioAlunoDTO {
    private List<HorarioAlunoLinhaDTO> linhas = new ArrayList<>();
    private Integer podefaltarSegunda;
    private Integer podefaltarTerca;
    private Integer podefaltarQuarta;
    private Integer podefaltarQuinta;
    private Integer podefaltarSexta;
}
