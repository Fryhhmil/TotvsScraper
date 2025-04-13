package br.gov.pi.tce.totvsscraper.dto;

import lombok.Data;

@Data
public class FaltaDTO {

    private String codigoDisciplina;

    private String codigoTurma;

    private String nomeMateria;

    private Integer faltas;

    private Integer podeFaltar;

    private Double percentual;
}
