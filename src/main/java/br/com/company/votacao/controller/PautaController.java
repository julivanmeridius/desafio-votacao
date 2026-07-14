package br.com.company.votacao.controller;

import br.com.company.votacao.dto.*;
import br.com.company.votacao.service.PautaService;
import br.com.company.votacao.service.ResultadoService;
import br.com.company.votacao.service.SessaoVotacaoService;
import br.com.company.votacao.service.VotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static br.com.company.votacao.constants.VotacaoConstants.API_PAUTAS_BASE_PATH;
import static br.com.company.votacao.constants.VotacaoConstants.API_RESULTADO_PATH;
import static br.com.company.votacao.constants.VotacaoConstants.API_SESSOES_PATH;
import static br.com.company.votacao.constants.VotacaoConstants.API_VOTOS_PATH;
import static br.com.company.votacao.constants.VotacaoConstants.OP_ABRIR_SESSAO;
import static br.com.company.votacao.constants.VotacaoConstants.OP_CRIAR_PAUTA;
import static br.com.company.votacao.constants.VotacaoConstants.OP_OBTER_RESULTADO;
import static br.com.company.votacao.constants.VotacaoConstants.OP_REGISTRAR_VOTO;
import static br.com.company.votacao.constants.VotacaoConstants.TAG_PAUTAS_DESCRIPTION;
import static br.com.company.votacao.constants.VotacaoConstants.TAG_PAUTAS_NAME;

@RestController
@RequestMapping(API_PAUTAS_BASE_PATH)
@RequiredArgsConstructor
@Tag(name = TAG_PAUTAS_NAME, description = TAG_PAUTAS_DESCRIPTION)
public class PautaController {

    private final PautaService pautaService;
    private final SessaoVotacaoService sessaoVotacaoService;
    private final VotoService votoService;
    private final ResultadoService resultadoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = OP_CRIAR_PAUTA)
    public PautaResponseDTO criar(@Valid @RequestBody PautaRequestDTO dto) {
        return pautaService.criar(dto);
    }

    @PostMapping(API_SESSOES_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = OP_ABRIR_SESSAO)
    public SessaoVotacaoResponseDTO abrirSessao(
            @PathVariable Long pautaId,
            @Valid @RequestBody(required = false) SessaoVotacaoRequestDTO dto
    ) {
        return sessaoVotacaoService.abrir(pautaId, dto);
    }

    @PostMapping(API_VOTOS_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = OP_REGISTRAR_VOTO)
    public VotoResponseDTO votar(
            @PathVariable Long pautaId,
            @Valid @RequestBody VotoRequestDTO dto
    ) {
        return votoService.votar(pautaId, dto);
    }

    @GetMapping(API_RESULTADO_PATH)
    @Operation(summary = OP_OBTER_RESULTADO)
    public ResultadoResponseDTO resultado(@PathVariable Long pautaId) {
        return resultadoService.obter(pautaId);
    }
}
