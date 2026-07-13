package br.com.company.votacao.service;

import br.com.company.votacao.dto.VotoRequestDTO;
import br.com.company.votacao.dto.VotoResponseDTO;
import br.com.company.votacao.mapper.VotoMapper;
import br.com.company.votacao.model.Associado;
import br.com.company.votacao.model.Pauta;
import br.com.company.votacao.model.SessaoVotacao;
import br.com.company.votacao.model.Voto;
import br.com.company.votacao.repository.AssociadoRepository;
import br.com.company.votacao.repository.PautaRepository;
import br.com.company.votacao.repository.SessaoVotacaoRepository;
import br.com.company.votacao.repository.VotoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@ExtendWith(MockitoExtension.class)
class VotoServiceTest {

    @Mock
    private PautaRepository pautaRepository;

    @Mock
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @Mock
    private AssociadoRepository associadoRepository;

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private VotoMapper votoMapper;

    @InjectMocks
    private VotoService votoService;

    @Test
    void votar_shouldSaveVotoAndReturnResponseDTO_whenAllConditionsMet() {
        var pautaId = 1L;
        var associadoId = 10L;
        var dto = new VotoRequestDTO(associadoId, "Sim");

        var pauta = new Pauta();
        pauta.setId(pautaId);

        var sessao = new SessaoVotacao();
        sessao.setId(5L);

        var associado = new Associado();
        associado.setId(associadoId);

        var entity = new Voto();
        var savedEntity = new Voto();
        savedEntity.setId(100L);
        savedEntity.setRecebidoEm(OffsetDateTime.now());

        var expectedResponse = new VotoResponseDTO(100L, savedEntity.getRecebidoEm());

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.findActivaByPautaId(pautaId)).thenReturn(Optional.of(sessao));
        when(votoRepository.existsByPautaIdAndAssociadoId(pautaId, associadoId)).thenReturn(false);
        when(associadoRepository.findById(associadoId)).thenReturn(Optional.of(associado));
        when(votoMapper.toEntity(pauta, sessao, associado, dto)).thenReturn(entity);
        when(votoRepository.save(entity)).thenReturn(savedEntity);
        when(votoMapper.toResponseDTO(savedEntity)).thenReturn(expectedResponse);

        var result = votoService.votar(pautaId, dto);

        assertThat(result).isEqualTo(expectedResponse);
        verify(pautaRepository).findById(pautaId);
        verify(sessaoVotacaoRepository).findActivaByPautaId(pautaId);
        verify(votoRepository).existsByPautaIdAndAssociadoId(pautaId, associadoId);
        verify(associadoRepository).findById(associadoId);
        verify(votoMapper).toEntity(pauta, sessao, associado, dto);
        verify(votoRepository).save(entity);
        verify(votoMapper).toResponseDTO(savedEntity);
    }

    @Test
    void votar_shouldThrowNotFound_whenPautaDoesNotExist() {
        var pautaId = 99L;
        var dto = new VotoRequestDTO(1L, "Sim");

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> votoService.votar(pautaId, dto))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    var rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(NOT_FOUND);
                    assertThat(rse.getReason()).isEqualTo("Pauta não encontrada");
                });

        verify(pautaRepository).findById(pautaId);
        verifyNoInteractions(sessaoVotacaoRepository, votoRepository, associadoRepository, votoMapper);
    }

    @Test
    void votar_shouldThrowUnprocessableEntity_whenNoActiveSessao() {
        var pautaId = 1L;
        var dto = new VotoRequestDTO(1L, "Sim");

        var pauta = new Pauta();
        pauta.setId(pautaId);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.findActivaByPautaId(pautaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> votoService.votar(pautaId, dto))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    var rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(UNPROCESSABLE_ENTITY);
                    assertThat(rse.getReason()).isEqualTo("Nenhuma sessão ativa encontrada para esta pauta");
                });

        verifyNoInteractions(votoRepository, associadoRepository, votoMapper);
    }

    @Test
    void votar_shouldThrowConflict_whenAssociadoAlreadyVoted() {
        var pautaId = 1L;
        var associadoId = 10L;
        var dto = new VotoRequestDTO(associadoId, "Não");

        var pauta = new Pauta();
        pauta.setId(pautaId);

        var sessao = new SessaoVotacao();
        sessao.setId(5L);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.findActivaByPautaId(pautaId)).thenReturn(Optional.of(sessao));
        when(votoRepository.existsByPautaIdAndAssociadoId(pautaId, associadoId)).thenReturn(true);

        assertThatThrownBy(() -> votoService.votar(pautaId, dto))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    var rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(CONFLICT);
                    assertThat(rse.getReason()).isEqualTo("Associado já votou nesta pauta");
                });

        verifyNoInteractions(associadoRepository, votoMapper);
    }

    @Test
    void votar_shouldThrowNotFound_whenAssociadoDoesNotExist() {
        var pautaId = 1L;
        var associadoId = 99L;
        var dto = new VotoRequestDTO(associadoId, "Sim");

        var pauta = new Pauta();
        pauta.setId(pautaId);

        var sessao = new SessaoVotacao();
        sessao.setId(5L);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.findActivaByPautaId(pautaId)).thenReturn(Optional.of(sessao));
        when(votoRepository.existsByPautaIdAndAssociadoId(pautaId, associadoId)).thenReturn(false);
        when(associadoRepository.findById(associadoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> votoService.votar(pautaId, dto))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    var rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(NOT_FOUND);
                    assertThat(rse.getReason()).isEqualTo("Associado não encontrado");
                });

        verifyNoInteractions(votoMapper);
    }
}
