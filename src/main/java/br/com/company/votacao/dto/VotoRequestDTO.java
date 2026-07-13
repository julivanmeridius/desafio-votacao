package br.com.company.votacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record VotoRequestDTO(
        @NotNull @Positive Long associadoId,
        @NotBlank @Pattern(regexp = "Sim|Não", message = "Voto deve ser 'Sim' ou 'Não'") String voto
) {
}
