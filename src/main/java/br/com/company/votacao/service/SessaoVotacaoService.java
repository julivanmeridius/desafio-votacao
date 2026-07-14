package br.com.company.votacao.service;

import br.com.company.votacao.dto.SessaoVotacaoRequestDTO;
import br.com.company.votacao.dto.SessaoVotacaoResponseDTO;
import br.com.company.votacao.mapper.SessaoVotacaoMapper;
import br.com.company.votacao.repository.PautaRepository;
import br.com.company.votacao.repository.SessaoVotacaoRepository;
import lombok.RequiredArgsConstructor;
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
