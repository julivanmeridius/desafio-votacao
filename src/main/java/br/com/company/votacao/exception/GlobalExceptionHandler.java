package br.com.company.votacao.exception;

import br.com.company.votacao.dto.ValidationErrorResponseDTO;
import br.com.company.votacao.dto.ValidationErrorResponseDTO.FieldError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponseDTO handleValidation(MethodArgumentNotValidException ex) {
        List<FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new FieldError(e.getField(), e.getDefaultMessage()))
                .toList();
        return new ValidationErrorResponseDTO(errors);
    }
}
