package br.com.company.votacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import static br.com.company.votacao.constants.VotacaoConstants.VOTO_REGEX;
import static br.com.company.votacao.constants.VotacaoConstants.VOTO_VALIDATION_MESSAGE;

public record VotoRequestDTO(
        @NotNull @Positive Long associadoId,
        @NotBlank @Pattern(regexp = VOTO_REGEX, message = VOTO_VALIDATION_MESSAGE) String voto
) {
}
