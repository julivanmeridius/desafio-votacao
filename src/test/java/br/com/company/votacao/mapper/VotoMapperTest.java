package br.com.company.votacao.mapper;

import br.com.company.votacao.dto.VotoRequestDTO;
import br.com.company.votacao.model.Associado;
import br.com.company.votacao.model.Pauta;
import br.com.company.votacao.model.SessaoVotacao;
import br.com.company.votacao.model.Voto;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class VotoMapperTest {

    private final VotoMapper mapper = new VotoMapper();

    @Test
    void toEntity_shouldMapAllFields() {
        var pauta = new Pauta();
        pauta.setId(1L);

        var sessao = new SessaoVotacao();
        sessao.setId(5L);

        var associado = new Associado();
        associado.setId(10L);

        var dto = new VotoRequestDTO(10L, "Sim");

        var result = mapper.toEntity(pauta, sessao, associado, dto);

        assertThat(result.getPauta()).isEqualTo(pauta);
        assertThat(result.getSessaoVotacao()).isEqualTo(sessao);
        assertThat(result.getAssociado()).isEqualTo(associado);
        assertThat(result.getVoto()).isEqualTo("Sim");
        assertThat(result.getRecebidoEm()).isNotNull();
    }

    @Test
    void toResponseDTO_shouldMapIdAndRecebidoEm() {
        var recebidoEm = OffsetDateTime.now();
        var voto = new Voto();
        voto.setId(100L);
        voto.setRecebidoEm(recebidoEm);

        var result = mapper.toResponseDTO(voto);

        assertThat(result.votoId()).isEqualTo(100L);
        assertThat(result.recebidoEm()).isEqualTo(recebidoEm);
    }
}
