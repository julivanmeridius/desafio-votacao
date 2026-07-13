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

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

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
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Pauta não encontrada"));

        var sessaoVotacao = sessaoVotacaoRepository.findActivaByPautaId(pautaId)
                .orElseThrow(() -> new ResponseStatusException(UNPROCESSABLE_ENTITY, "Nenhuma sessão ativa encontrada para esta pauta"));

        if (votoRepository.existsByPautaIdAndAssociadoId(pautaId, dto.associadoId())) {
            throw new ResponseStatusException(CONFLICT, "Associado já votou nesta pauta");
        }

        var associado = associadoRepository.findById(dto.associadoId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Associado não encontrado"));

        var voto = votoMapper.toEntity(pauta, sessaoVotacao, associado, dto);
        var saved = votoRepository.save(voto);

        return votoMapper.toResponseDTO(saved);
    }
}
