package br.gov.pi.tce.totvsscraper.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "horario_aluno")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HorarioAluno {

    @Id
    @JsonProperty("id")
    @JsonAlias(value = {"IDHORARIOTURMA"})
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("diaSemana")
    @JsonAlias(value = {"DIASEMANA"})
    private String diaSemana;

    @JsonProperty("horaInicial")
    @JsonAlias(value = {"HORAINICIAL"})
    private String horaInicial;

    @JsonProperty("horaFinal")
    @JsonAlias(value = {"HORAFINAL"})
    private String horaFinal;

    @JsonProperty("codTurma")
    @JsonAlias(value = {"CODTURMA"})
    private String codTurma;

    @JsonProperty("codDisc")
    @JsonAlias(value = {"CODDISC"})
    private String codDisc;

    @JsonProperty("nome")
    @JsonAlias(value = {"NOME"})
    private String nome;

    @JsonProperty("idTurmaDisc")
    @JsonAlias(value = {"IDTURMADISC"})
    private Integer idTurmaDisc;

    @JsonProperty("impBoletim")
    @JsonAlias(value = {"IMPBOLETIM"})
    private String impBoletim;
}
