package br.com.company.votacao.service;

import br.com.company.votacao.kafka.ResultadoPublisher;
import br.com.company.votacao.model.StatusSessao;
import br.com.company.votacao.repository.VotoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import static br.com.company.votacao.constants.VotacaoConstants.RESULTADO_JSON_FORMAT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultadoPublicacaoServiceTest {

    @Mock private VotoRepository votoRepository;
    @Mock private ResultadoPublisher resultadoPublisher;
    @Mock private ObjectMapper objectMapper;

    @InjectMocks
    private ResultadoPublicacaoService resultadoPublicacaoService;

    @Test
    void publicarResultadoFinal_shouldPublishSerializedJson_whenSerializationSucceeds() throws Exception {
        var pautaId = 1L;
        var projection = mock(VotoRepository.ResultadoVotacaoProjection.class);
        var expectedJson = "{\"sim\":3,\"nao\":1,\"total\":4,\"status\":\"ENCERRADA\"}";

        when(votoRepository.countResultadoByPautaId(pautaId)).thenReturn(projection);
        when(projection.getSimCount()).thenReturn(3L);
        when(projection.getNaoCount()).thenReturn(1L);
        when(objectMapper.writeValueAsString(any())).thenReturn(expectedJson);

        resultadoPublicacaoService.publicarResultadoFinal(pautaId);

        var messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(resultadoPublisher).publishResultado(messageCaptor.capture());
        assertThat(messageCaptor.getValue()).isEqualTo(expectedJson);
    }

    @Test
    void publicarResultadoFinal_shouldPublishFallbackJson_whenSerializationFails() throws Exception {
        var pautaId = 1L;
        var projection = mock(VotoRepository.ResultadoVotacaoProjection.class);

        when(votoRepository.countResultadoByPautaId(pautaId)).thenReturn(projection);
        when(projection.getSimCount()).thenReturn(2L);
        when(projection.getNaoCount()).thenReturn(0L);
        when(objectMapper.writeValueAsString(any())).thenThrow(mock(tools.jackson.core.JacksonException.class));

        resultadoPublicacaoService.publicarResultadoFinal(pautaId);

        var messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(resultadoPublisher).publishResultado(messageCaptor.capture());
        var expected = String.format(RESULTADO_JSON_FORMAT, 2L, 0L, 2L, StatusSessao.ENCERRADA);
        assertThat(messageCaptor.getValue()).isEqualTo(expected);
    }
}
