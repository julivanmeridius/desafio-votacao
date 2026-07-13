package br.com.company.votacao.mapper;

import br.com.company.votacao.dto.PautaRequestDTO;
import br.com.company.votacao.dto.PautaResponseDTO;
import br.com.company.votacao.model.Pauta;
import org.springframework.stereotype.Component;

@Component
public class PautaMapper {

    public Pauta toEntity(PautaRequestDTO dto) {
        Pauta pauta = new Pauta();
        pauta.setTitulo(dto.titulo());
        pauta.setDescricao(dto.descricao());
        return pauta;
    }

    public PautaResponseDTO toResponseDTO(Pauta pauta) {
        return new PautaResponseDTO(pauta.getId());
    }
}
