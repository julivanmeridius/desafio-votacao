package br.com.company.votacao.dto;

import java.util.List;

public record ValidationErrorResponseDTO(List<FieldError> errors) {

    public record FieldError(String field, String message) {
    }
}
