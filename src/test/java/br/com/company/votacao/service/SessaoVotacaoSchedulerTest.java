package br.com.company.votacao.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SessaoVotacaoSchedulerTest {

    @Mock
    private SessaoVotacaoService sessaoVotacaoService;

    @InjectMocks
    private SessaoVotacaoScheduler sessaoVotacaoScheduler;

    @Test
    void encerrarSessoesExpiradas_shouldDelegateToService() {
        sessaoVotacaoScheduler.encerrarSessoesExpiradas();
        verify(sessaoVotacaoService).encerrarSessoesExpiradas();
    }
}
