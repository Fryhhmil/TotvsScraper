package br.gov.pi.tce.totvsscraper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Falta {

    @JsonProperty("CODTURMA")
    private String codTurma;

    @JsonProperty("CODDISC")
    private String codDisc;

    @JsonProperty("Disciplina")
    private String disciplina;

    @JsonProperty("Situação")
    private String situacao;

    @JsonProperty("1 - 1º BIM")
    private String primeiroBimestre;

    @JsonProperty("2 - 2º BIM")
    private String segundoBimestre;

    @JsonProperty("3 - TOTAL FALTAS")
    private String totalFaltas;

    @JsonProperty("PERCENTUAL")
    private double percentual;

    @JsonProperty("IDTURMADISC")
    private int idTurmaDisc;

    @JsonProperty("SITUACAOFALTAS")
    private int situacaoFaltas;
}
