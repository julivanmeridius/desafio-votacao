package br.com.company.votacao.service;

import br.com.company.votacao.dto.PautaRequestDTO;
import br.com.company.votacao.dto.PautaResponseDTO;
import br.com.company.votacao.mapper.PautaMapper;
import br.com.company.votacao.repository.PautaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PautaService {

    private final PautaRepository pautaRepository;
    private final PautaMapper pautaMapper;

    @Transactional
    public PautaResponseDTO criar(PautaRequestDTO dto) {
        var pauta = pautaMapper.toEntity(dto);
        var saved = pautaRepository.save(pauta);
        return pautaMapper.toResponseDTO(saved);
    }
}
