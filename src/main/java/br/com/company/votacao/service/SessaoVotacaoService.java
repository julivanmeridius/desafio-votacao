package br.com.company.votacao.service;

import br.com.company.votacao.dto.SessaoVotacaoRequestDTO;
import br.com.company.votacao.dto.SessaoVotacaoResponseDTO;
import br.com.company.votacao.mapper.SessaoVotacaoMapper;
import br.com.company.votacao.repository.PautaRepository;
import br.com.company.votacao.repository.SessaoVotacaoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static br.com.company.votacao.constants.VotacaoConstants.PAUTA_NAO_ENCONTRADA;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SessaoVotacaoService {

    private static final Logger log = LoggerFactory.getLogger(SessaoVotacaoService.class);

    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final PautaRepository pautaRepository;
    private final SessaoVotacaoMapper sessaoVotacaoMapper;
    private final ResultadoPublicacaoService resultadoPublicacaoService;

    @Transactional
    public SessaoVotacaoResponseDTO abrir(Long pautaId, SessaoVotacaoRequestDTO dto) {
        var pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, PAUTA_NAO_ENCONTRADA));

        var sessaoVotacao = sessaoVotacaoMapper.toEntity(pauta, dto);
        var saved = sessaoVotacaoRepository.save(sessaoVotacao);

        log.info("Sessao de votacao aberta - pautaId={}, sessaoId={}, duracaoSegundos={}, fechaAbertura={}",
                pauta.getId(), saved.getId(), saved.getDuracaoSegundos(), saved.getFechaAbertura());

        return sessaoVotacaoMapper.toResponseDTO(saved);
    }

    @Transactional
    public void encerrarSessoesExpiradas() {
        var sessoesExpiradas = sessaoVotacaoRepository.findSessoesExpiradasParaEncerrar();

        if (sessoesExpiradas.isEmpty()) {
            return;
        }

        var pautaIdsEncerradas = new ArrayList<Long>();
        var agora = OffsetDateTime.now();

        for (var sessao : sessoesExpiradas) {
            sessao.setEncerradaEm(agora);
            pautaIdsEncerradas.add(sessao.getPauta().getId());
        }

        sessaoVotacaoRepository.saveAll(sessoesExpiradas);

        log.info("Sessoes encerradas por expiracao - quantidade={}, pautaIds={}",
                sessoesExpiradas.size(), pautaIdsEncerradas);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                publicarResultadosEncerrados(pautaIdsEncerradas);
            }
        });
    }

    private void publicarResultadosEncerrados(List<Long> pautaIds) {
        for (Long pautaId : pautaIds) {
            resultadoPublicacaoService.publicarResultadoFinal(pautaId);
        }
    }
}
