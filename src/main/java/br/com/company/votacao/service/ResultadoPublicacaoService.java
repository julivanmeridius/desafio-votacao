package br.com.company.votacao.service;

import br.com.company.votacao.dto.ResultadoResponseDTO;
import br.com.company.votacao.kafka.ResultadoPublisher;
import br.com.company.votacao.model.StatusSessao;
import br.com.company.votacao.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class ResultadoPublicacaoService {

    private static final Logger log = LoggerFactory.getLogger(ResultadoPublicacaoService.class);

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

        log.info("Publicando resultado final - pautaId={}, sim={}, nao={}, total={}",
                pautaId, simCount, naoCount, simCount + naoCount);

        resultadoPublisher.publishResultado(buildResultadoMessage(dto, pautaId));
    }

    private String buildResultadoMessage(ResultadoResponseDTO dto, Long pautaId) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JacksonException e) {
            log.warn("Falha ao serializar resultado com Jackson; aplicando fallback manual - pautaId={}", pautaId, e);
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

