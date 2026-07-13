package br.com.company.votacao.service;

import br.com.company.votacao.dto.PautaRequestDTO;
import br.com.company.votacao.dto.PautaResponseDTO;
import br.com.company.votacao.mapper.PautaMapper;
import br.com.company.votacao.model.Pauta;
import br.com.company.votacao.repository.PautaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PautaServiceTest {

    @Mock
    private PautaRepository pautaRepository;

    @Mock
    private PautaMapper pautaMapper;

    @InjectMocks
    private PautaService pautaService;

    @Test
    void criar_shouldSavePautaAndReturnResponseDTO() {
        var request = new PautaRequestDTO("Titulo", "Descricao");
        var entity = new Pauta();
        var savedEntity = new Pauta();
        savedEntity.setId(1L);
        var expectedResponse = new PautaResponseDTO(1L);

        when(pautaMapper.toEntity(request)).thenReturn(entity);
        when(pautaRepository.save(entity)).thenReturn(savedEntity);
        when(pautaMapper.toResponseDTO(savedEntity)).thenReturn(expectedResponse);

        var result = pautaService.criar(request);

        assertThat(result).isEqualTo(expectedResponse);
        verify(pautaMapper).toEntity(request);
        verify(pautaRepository).save(entity);
        verify(pautaMapper).toResponseDTO(savedEntity);
    }
}
