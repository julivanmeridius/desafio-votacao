package br.com.company.votacao.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static br.com.company.votacao.constants.VotacaoConstants.SESSAO_ENCERRAMENTO_DELAY_PROPERTY;

@Component
@RequiredArgsConstructor
public class SessaoVotacaoScheduler {

    private final SessaoVotacaoService sessaoVotacaoService;

    @Scheduled(fixedDelayString = SESSAO_ENCERRAMENTO_DELAY_PROPERTY)
    public void encerrarSessoesExpiradas() {
        sessaoVotacaoService.encerrarSessoesExpiradas();
    }
}
