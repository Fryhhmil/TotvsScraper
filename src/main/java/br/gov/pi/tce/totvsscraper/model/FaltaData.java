package br.gov.pi.tce.totvsscraper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class FaltaData {
    @JsonProperty("FaltasEtapa")
    List<Falta> faltasEtapa;
}
