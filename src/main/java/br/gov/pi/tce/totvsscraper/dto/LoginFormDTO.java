package br.gov.pi.tce.totvsscraper.dto;

import lombok.Data;

@Data
public class LoginFormDTO {
    private String cpf;
    private String senha;

    public String getCpf() {
        if (cpf == null) return null;
        // Adiciona zeros à esquerda se o cpf tiver menos de 11 dígitos
        return String.format("%011d", Long.parseLong(cpf));
    }
}
