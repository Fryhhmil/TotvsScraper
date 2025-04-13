package br.gov.pi.tce.totvsscraper.model;

import br.gov.pi.tce.totvsscraper.dto.FaltaDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "RelacaoFaltas")
@Getter
@Setter
@NoArgsConstructor
public class RelacaoFalta {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String codigoDisciplina;

    @Column(nullable = false)
    private String codigoTurma;

    @Column(nullable = false)
    private String nomeMateria;

    @Column()
    private Integer diasParaFaltar;

    public RelacaoFalta(FaltaDTO faltaDTO) {
        this.codigoDisciplina = faltaDTO.getCodigoDisciplina();
        this.codigoTurma = faltaDTO.getCodigoTurma();
        this.nomeMateria = faltaDTO.getNomeMateria();
        this.diasParaFaltar = null;
    }

}
