package br.com.company.votacao.mapper;

import br.com.company.votacao.dto.SessaoVotacaoRequestDTO;
import br.com.company.votacao.model.Pauta;
import br.com.company.votacao.model.SessaoVotacao;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessaoVotacaoMapperTest {

    private final SessaoVotacaoMapper mapper = new SessaoVotacaoMapper();

    @Test
    void toEntity_shouldMapPautaAndDuracaoFromDTO() {
        var pauta = new Pauta();
        pauta.setId(1L);
        var dto = new SessaoVotacaoRequestDTO(120L);

        var sessao = mapper.toEntity(pauta, dto);

        assertThat(sessao.getPauta()).isEqualTo(pauta);
        assertThat(sessao.getDuracaoSegundos()).isEqualTo(120L);
        assertThat(sessao.getTempoAbertura()).isNotNull();
        assertThat(sessao.getFechaAbertura()).isEqualTo(sessao.getTempoAbertura().plusSeconds(120L));
        assertThat(sessao.getId()).isNull();
        assertThat(sessao.getEncerradaEm()).isNull();
    }

    @Test
    void toEntity_shouldUseDefaultDuration_whenDTOIsNull() {
        var pauta = new Pauta();
        pauta.setId(1L);

        var sessao = mapper.toEntity(pauta, null);

        assertThat(sessao.getPauta()).isEqualTo(pauta);
        assertThat(sessao.getDuracaoSegundos()).isEqualTo(60L);
        assertThat(sessao.getTempoAbertura()).isNotNull();
        assertThat(sessao.getFechaAbertura()).isEqualTo(sessao.getTempoAbertura().plusSeconds(60L));
    }

    @Test
    void toEntity_shouldUseDefaultDuration_whenDuracaoSegundosIsNull() {
        var pauta = new Pauta();
        pauta.setId(1L);
        var dto = new SessaoVotacaoRequestDTO(null);

        var sessao = mapper.toEntity(pauta, dto);

        assertThat(sessao.getPauta()).isEqualTo(pauta);
        assertThat(sessao.getDuracaoSegundos()).isEqualTo(60L);
        assertThat(sessao.getTempoAbertura()).isNotNull();
        assertThat(sessao.getFechaAbertura()).isEqualTo(sessao.getTempoAbertura().plusSeconds(60L));
    }

    @Test
    void toResponseDTO_shouldMapFieldsFromSessaoVotacao() {
        var pauta = new Pauta();
        pauta.setId(1L);

        var sessao = new SessaoVotacao();
        sessao.setId(10L);
        sessao.setPauta(pauta);

        var tempoAbertura = java.time.OffsetDateTime.now();
        var fechaAbertura = tempoAbertura.plusSeconds(90L);

        sessao.setTempoAbertura(tempoAbertura);
        sessao.setFechaAbertura(fechaAbertura);
        sessao.setDuracaoSegundos(90L);

        var dto = mapper.toResponseDTO(sessao);

        assertThat(dto.sessaoId()).isEqualTo(10L);
        assertThat(dto.tempoAbertura()).isEqualTo(tempoAbertura);
        assertThat(dto.fechaAbertura()).isEqualTo(fechaAbertura);
    }
}
