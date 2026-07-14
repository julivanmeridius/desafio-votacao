package br.com.company.votacao.service;

import br.com.company.votacao.model.SessaoVotacao;
import br.com.company.votacao.model.StatusSessao;
import br.com.company.votacao.repository.PautaRepository;
import br.com.company.votacao.repository.SessaoVotacaoRepository;
import br.com.company.votacao.repository.VotoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static br.com.company.votacao.constants.VotacaoConstants.PAUTA_NAO_ENCONTRADA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class ResultadoServiceTest {

    @Mock private PautaRepository pautaRepository;
    @Mock private SessaoVotacaoRepository sessaoVotacaoRepository;
    @Mock private VotoRepository votoRepository;

    @InjectMocks
    private ResultadoService resultadoService;

    @Test
    void obter_shouldReturnResultadoWithStatusAberta_whenSessaoIsActive() {
        var pautaId = 1L;
        var projection = mock(VotoRepository.ResultadoVotacaoProjection.class);

        when(pautaRepository.existsById(pautaId)).thenReturn(true);
        when(votoRepository.countResultadoByPautaId(pautaId)).thenReturn(projection);
        when(projection.getSimCount()).thenReturn(3L);
        when(projection.getNaoCount()).thenReturn(1L);
        when(sessaoVotacaoRepository.findActivaByPautaId(pautaId)).thenReturn(Optional.of(new SessaoVotacao()));

        var result = resultadoService.obter(pautaId);

        assertThat(result.simCount()).isEqualTo(3L);
        assertThat(result.naoCount()).isEqualTo(1L);
        assertThat(result.total()).isEqualTo(4L);
        assertThat(result.status()).isEqualTo(StatusSessao.ABERTA);

        verify(pautaRepository).existsById(pautaId);
        verify(votoRepository).countResultadoByPautaId(pautaId);
        verify(sessaoVotacaoRepository).findActivaByPautaId(pautaId);
    }

    @Test
    void obter_shouldReturnResultadoWithStatusEncerrada_whenNoActiveSessao() {
        var pautaId = 1L;
        var projection = mock(VotoRepository.ResultadoVotacaoProjection.class);

        when(pautaRepository.existsById(pautaId)).thenReturn(true);
        when(votoRepository.countResultadoByPautaId(pautaId)).thenReturn(projection);
        when(projection.getSimCount()).thenReturn(0L);
        when(projection.getNaoCount()).thenReturn(2L);
        when(sessaoVotacaoRepository.findActivaByPautaId(pautaId)).thenReturn(Optional.empty());

        var result = resultadoService.obter(pautaId);

        assertThat(result.simCount()).isEqualTo(0L);
        assertThat(result.naoCount()).isEqualTo(2L);
        assertThat(result.total()).isEqualTo(2L);
        assertThat(result.status()).isEqualTo(StatusSessao.ENCERRADA);
    }

    @Test
    void obter_shouldReturnZeroCountsAndStatusEncerrada_whenNoVotesAndNoSession() {
        var pautaId = 1L;
        var projection = mock(VotoRepository.ResultadoVotacaoProjection.class);

        when(pautaRepository.existsById(pautaId)).thenReturn(true);
        when(votoRepository.countResultadoByPautaId(pautaId)).thenReturn(projection);
        when(projection.getSimCount()).thenReturn(0L);
        when(projection.getNaoCount()).thenReturn(0L);
        when(sessaoVotacaoRepository.findActivaByPautaId(pautaId)).thenReturn(Optional.empty());

        var result = resultadoService.obter(pautaId);

        assertThat(result.simCount()).isEqualTo(0L);
        assertThat(result.naoCount()).isEqualTo(0L);
        assertThat(result.total()).isEqualTo(0L);
        assertThat(result.status()).isEqualTo(StatusSessao.ENCERRADA);
    }

    @Test
    void obter_shouldThrowNotFound_whenPautaDoesNotExist() {
        var pautaId = 99L;

        when(pautaRepository.existsById(pautaId)).thenReturn(false);

        assertThatThrownBy(() -> resultadoService.obter(pautaId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    var rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(NOT_FOUND);
                    assertThat(rse.getReason()).isEqualTo(PAUTA_NAO_ENCONTRADA);
                });

        verify(pautaRepository).existsById(pautaId);
        verifyNoInteractions(votoRepository, sessaoVotacaoRepository);
    }
}
