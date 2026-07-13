package br.com.company.votacao.service;

import br.com.company.votacao.dto.SessaoVotacaoRequestDTO;
import br.com.company.votacao.dto.SessaoVotacaoResponseDTO;
import br.com.company.votacao.mapper.SessaoVotacaoMapper;
import br.com.company.votacao.repository.PautaRepository;
import br.com.company.votacao.repository.SessaoVotacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SessaoVotacaoService {

    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final PautaRepository pautaRepository;
    private final SessaoVotacaoMapper sessaoVotacaoMapper;

    @Transactional
    public SessaoVotacaoResponseDTO abrir(Long pautaId, SessaoVotacaoRequestDTO dto) {
        var pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Pauta não encontrada"));

        var sessaoVotacao = sessaoVotacaoMapper.toEntity(pauta, dto);
        var saved = sessaoVotacaoRepository.save(sessaoVotacao);

        return sessaoVotacaoMapper.toResponseDTO(saved);
    }
}
