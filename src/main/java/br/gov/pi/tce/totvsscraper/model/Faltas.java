package br.gov.pi.tce.totvsscraper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Faltas {
    FaltaData data;

    Object messages;

    Object length;

    @JsonProperty("HttpStatusCode")
    Object httpStatusCode;
}
