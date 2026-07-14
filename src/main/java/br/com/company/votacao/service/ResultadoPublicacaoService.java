package br.com.company.votacao.service;

import br.com.company.votacao.dto.ResultadoResponseDTO;
import br.com.company.votacao.kafka.ResultadoPublisher;
import br.com.company.votacao.model.StatusSessao;
import br.com.company.votacao.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class ResultadoPublicacaoService {

    private final VotoRepository votoRepository;
    private final ResultadoPublisher resultadoPublisher;
    private final ObjectMapper objectMapper;

    public void publicarResultadoFinal(Long pautaId) {
        VotoRepository.ResultadoVotacaoProjection counts = votoRepository.countResultadoByPautaId(pautaId);

        long simCount = counts.getSimCount();
        long naoCount = counts.getNaoCount();

        var dto = new ResultadoResponseDTO(
                simCount,
                naoCount,
                simCount + naoCount,
                StatusSessao.ENCERRADA
        );

        resultadoPublisher.publishResultado(buildResultadoMessage(dto));
    }

    private String buildResultadoMessage(ResultadoResponseDTO dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JacksonException e) {
            return String.format(
                    "{\"sim\":%d,\"nao\":%d,\"total\":%d,\"status\":\"%s\"}",
                    dto.simCount(),
                    dto.naoCount(),
                    dto.total(),
                    dto.status()
            );
        }
    }
}

