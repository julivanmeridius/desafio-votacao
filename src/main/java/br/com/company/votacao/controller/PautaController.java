package br.com.company.votacao.controller;

import br.com.company.votacao.mapper.SessaoVotacaoMapper;

import br.com.company.votacao.dto.PautaRequestDTO;
import br.com.company.votacao.dto.PautaResponseDTO;
import br.com.company.votacao.dto.SessaoVotacaoRequestDTO;
import br.com.company.votacao.dto.SessaoVotacaoResponseDTO;
import br.com.company.votacao.service.PautaService;
import br.com.company.votacao.service.SessaoVotacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/pautas")
@RequiredArgsConstructor
@Tag(name = "Pautas", description = "Gerenciamento de pautas")
public class PautaController {

    private final PautaService pautaService;
    private final SessaoVotacaoService sessaoVotacaoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar nova pauta para votação")
    public PautaResponseDTO criar(@Valid @RequestBody PautaRequestDTO dto) {
        return pautaService.criar(dto);
    }

    @PostMapping("/{pautaId}/sessoes")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Realizar a abertura de sessão de votação de uma pauta")
    public SessaoVotacaoResponseDTO abrirSessao(
            @PathVariable Long pautaId,
            @Valid @RequestBody(required = false) SessaoVotacaoRequestDTO dto
    ) {
        return sessaoVotacaoService.abrir(pautaId, dto);
    }
}
