package br.gov.pi.tce.totvsscraper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OrigemDTO {
    @JsonProperty("data")
    FaltaData data;

    Object messages;

    Object length;

    @JsonProperty("HttpStatusCode")
    Object httpStatusCode;
}
