package br.com.company.votacao.service;

import br.com.company.votacao.dto.VotoRequestDTO;
import br.com.company.votacao.dto.VotoResponseDTO;
import br.com.company.votacao.mapper.VotoMapper;
import br.com.company.votacao.repository.AssociadoRepository;
import br.com.company.votacao.repository.PautaRepository;
import br.com.company.votacao.repository.SessaoVotacaoRepository;
import br.com.company.votacao.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static br.com.company.votacao.constants.VotacaoConstants.*;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class VotoService {

    private final PautaRepository pautaRepository;
    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final AssociadoRepository associadoRepository;
    private final VotoRepository votoRepository;
    private final VotoMapper votoMapper;

    @Transactional
    public VotoResponseDTO votar(Long pautaId, VotoRequestDTO dto) {
        var pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, PAUTA_NAO_ENCONTRADA));

        var sessaoVotacao = sessaoVotacaoRepository.findActivaByPautaId(pautaId)
                .orElseThrow(() -> new ResponseStatusException(UNPROCESSABLE_CONTENT, SESSAO_ATIVA_NAO_ENCONTRADA));

        if (votoRepository.existsByPautaIdAndAssociadoId(pautaId, dto.associadoId())) {
            throw new ResponseStatusException(CONFLICT, ASSOCIADO_JA_VOTOU);
        }

        var associado = associadoRepository.findById(dto.associadoId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, ASSOCIADO_NAO_ENCONTRADO));

        var voto = votoMapper.toEntity(pauta, sessaoVotacao, associado, dto);
        var saved = votoRepository.save(voto);

        return votoMapper.toResponseDTO(saved);
    }
}
