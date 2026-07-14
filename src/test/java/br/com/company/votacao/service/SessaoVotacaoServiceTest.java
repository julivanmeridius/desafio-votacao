package br.com.company.votacao.service;

import br.com.company.votacao.dto.SessaoVotacaoRequestDTO;
import br.com.company.votacao.dto.SessaoVotacaoResponseDTO;
import br.com.company.votacao.mapper.SessaoVotacaoMapper;
import br.com.company.votacao.model.Pauta;
import br.com.company.votacao.model.SessaoVotacao;
import br.com.company.votacao.repository.PautaRepository;
import br.com.company.votacao.repository.SessaoVotacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class SessaoVotacaoServiceTest {

    @Mock private SessaoVotacaoRepository sessaoVotacaoRepository;
    @Mock private PautaRepository pautaRepository;
    @Mock private SessaoVotacaoMapper sessaoVotacaoMapper;
    @Mock private ResultadoPublicacaoService resultadoPublicacaoService;

    @InjectMocks
    private SessaoVotacaoService sessaoVotacaoService;

    @Test
    void abrir_shouldSaveSessaoAndReturnResponseDTO_whenPautaExists() {
        var pautaId = 1L;
        var request = new SessaoVotacaoRequestDTO(120L);

        var pauta = new Pauta();
        pauta.setId(pautaId);

        var entity = new SessaoVotacao();
        entity.setPauta(pauta);

        var savedEntity = new SessaoVotacao();
        savedEntity.setId(10L);
        savedEntity.setPauta(pauta);

        var tempoAbertura = OffsetDateTime.now();
        var fechaAbertura = tempoAbertura.plusSeconds(120L);
        var expectedResponse = new SessaoVotacaoResponseDTO(10L, tempoAbertura, fechaAbertura);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoMapper.toEntity(pauta, request)).thenReturn(entity);
        when(sessaoVotacaoRepository.save(entity)).thenReturn(savedEntity);
        when(sessaoVotacaoMapper.toResponseDTO(savedEntity)).thenReturn(expectedResponse);

        var result = sessaoVotacaoService.abrir(pautaId, request);

        assertThat(result).isEqualTo(expectedResponse);
        verify(pautaRepository).findById(pautaId);
        verify(sessaoVotacaoMapper).toEntity(pauta, request);
        verify(sessaoVotacaoRepository).save(entity);
        verify(sessaoVotacaoMapper).toResponseDTO(savedEntity);
    }

    @Test
    void abrir_shouldThrowNotFound_whenPautaDoesNotExist() {
        var pautaId = 99L;
        var request = new SessaoVotacaoRequestDTO(120L);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessaoVotacaoService.abrir(pautaId, request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    var rse = (ResponseStatusException) exception;
                    assertThat(rse.getStatusCode()).isEqualTo(NOT_FOUND);
                    assertThat(rse.getReason()).isEqualTo("Pauta não encontrada");
                });

        verify(pautaRepository).findById(pautaId);
        verifyNoInteractions(sessaoVotacaoRepository, sessaoVotacaoMapper, resultadoPublicacaoService);
    }

    @Test
    void encerrarSessoesExpiradas_shouldDoNothing_whenNoExpiredSessoes() {
        when(sessaoVotacaoRepository.findSessoesExpiradasParaEncerrar()).thenReturn(List.of());

        sessaoVotacaoService.encerrarSessoesExpiradas();

        verify(sessaoVotacaoRepository, never()).saveAll(any());
        verifyNoInteractions(resultadoPublicacaoService);
    }

    @Test
    void encerrarSessoesExpiradas_shouldEncerrarAndPublishAfterCommit_whenExpiredSessaoExists() {
        var pauta = new Pauta();
        pauta.setId(1L);

        var sessao = new SessaoVotacao();
        sessao.setPauta(pauta);

        when(sessaoVotacaoRepository.findSessoesExpiradasParaEncerrar()).thenReturn(List.of(sessao));

        TransactionSynchronization sync;
        try (var mocked = mockStatic(TransactionSynchronizationManager.class)) {
            sessaoVotacaoService.encerrarSessoesExpiradas();

            var syncCaptor = ArgumentCaptor.forClass(TransactionSynchronization.class);
            mocked.verify(() -> TransactionSynchronizationManager.registerSynchronization(syncCaptor.capture()));
            sync = syncCaptor.getValue();
        }

        assertThat(sessao.getEncerradaEm()).isNotNull();
        verify(sessaoVotacaoRepository).saveAll(List.of(sessao));

        sync.afterCommit();
        verify(resultadoPublicacaoService).publicarResultadoFinal(1L);
    }

    @Test
    void encerrarSessoesExpiradas_shouldEncerrarMultipleSessoes_whenMultipleExpired() {
        var pauta1 = new Pauta();
        pauta1.setId(1L);
        var pauta2 = new Pauta();
        pauta2.setId(2L);

        var sessao1 = new SessaoVotacao();
        sessao1.setPauta(pauta1);
        var sessao2 = new SessaoVotacao();
        sessao2.setPauta(pauta2);

        var sessoesExpiradas = List.of(sessao1, sessao2);
        when(sessaoVotacaoRepository.findSessoesExpiradasParaEncerrar()).thenReturn(sessoesExpiradas);

        TransactionSynchronization sync;
        try (var mocked = mockStatic(TransactionSynchronizationManager.class)) {
            sessaoVotacaoService.encerrarSessoesExpiradas();

            var syncCaptor = ArgumentCaptor.forClass(TransactionSynchronization.class);
            mocked.verify(() -> TransactionSynchronizationManager.registerSynchronization(syncCaptor.capture()));
            sync = syncCaptor.getValue();
        }

        assertThat(sessao1.getEncerradaEm()).isNotNull();
        assertThat(sessao2.getEncerradaEm()).isNotNull();

        sync.afterCommit();
        verify(resultadoPublicacaoService).publicarResultadoFinal(1L);
        verify(resultadoPublicacaoService).publicarResultadoFinal(2L);
    }
}
