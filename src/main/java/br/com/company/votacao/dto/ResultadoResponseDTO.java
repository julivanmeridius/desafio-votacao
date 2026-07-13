package br.com.company.votacao.dto;

import br.com.company.votacao.model.StatusSessao;

public record ResultadoResponseDTO(
        long simCount,
        long naoCount,
        long total,
        StatusSessao status
) {
}
