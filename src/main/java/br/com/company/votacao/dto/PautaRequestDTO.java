package br.com.company.votacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PautaRequestDTO(
        @NotBlank @Size(max = 200) String titulo,
        @NotBlank String descricao
) {
}
