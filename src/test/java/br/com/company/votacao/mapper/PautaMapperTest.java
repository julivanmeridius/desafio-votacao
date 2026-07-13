package br.com.company.votacao.mapper;

import br.com.company.votacao.dto.PautaRequestDTO;
import br.com.company.votacao.model.Pauta;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PautaMapperTest {

    private final PautaMapper mapper = new PautaMapper();

    @Test
    void toEntity_shouldMapTituloAndDescricaoFromDTO() {
        var dto = new PautaRequestDTO("Titulo", "Descricao");

        var pauta = mapper.toEntity(dto);

        assertThat(pauta.getTitulo()).isEqualTo("Titulo");
        assertThat(pauta.getDescricao()).isEqualTo("Descricao");
        assertThat(pauta.getId()).isNull();
    }

    @Test
    void toResponseDTO_shouldMapIdFromPauta() {
        var pauta = new Pauta();
        pauta.setId(42L);

        var dto = mapper.toResponseDTO(pauta);

        assertThat(dto.pautaId()).isEqualTo(42L);
    }
}
