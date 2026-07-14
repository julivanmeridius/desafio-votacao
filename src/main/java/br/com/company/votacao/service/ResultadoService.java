package br.com.company.votacao.service;

import br.com.company.votacao.dto.ResultadoResponseDTO;
import br.com.company.votacao.model.StatusSessao;
import br.com.company.votacao.repository.PautaRepository;
import br.com.company.votacao.repository.SessaoVotacaoRepository;
import br.com.company.votacao.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static br.com.company.votacao.constants.VotacaoConstants.PAUTA_NAO_ENCONTRADA;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ResultadoService {

    private final PautaRepository pautaRepository;
    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final VotoRepository votoRepository;

    @Transactional(readOnly = true)
    public ResultadoResponseDTO obter(Long pautaId) {
        if (!pautaRepository.existsById(pautaId)) {
            throw new ResponseStatusException(NOT_FOUND, PAUTA_NAO_ENCONTRADA);
        }

        VotoRepository.ResultadoVotacaoProjection counts = votoRepository.countResultadoByPautaId(pautaId);
        long simCount = counts.getSimCount();
        long naoCount = counts.getNaoCount();

        var status = sessaoVotacaoRepository.findActivaByPautaId(pautaId).isPresent()
                ? StatusSessao.ABERTA
                : StatusSessao.ENCERRADA;

        return new ResultadoResponseDTO(simCount, naoCount, simCount + naoCount, status);
    }
}
