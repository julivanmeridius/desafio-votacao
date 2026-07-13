package br.com.company.votacao.mapper;

import br.com.company.votacao.dto.SessaoVotacaoRequestDTO;
import br.com.company.votacao.dto.SessaoVotacaoResponseDTO;
import br.com.company.votacao.model.Pauta;
import br.com.company.votacao.model.SessaoVotacao;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class SessaoVotacaoMapper {

    private static final long DURACAO_PADRAO_SEGUNDOS = 60L;

    public SessaoVotacao toEntity(Pauta pauta, SessaoVotacaoRequestDTO dto) {
        var tempoAbertura = OffsetDateTime.now();
        var duracaoSegundos = obterDuracaoSegundos(dto);

        var sessaoVotacao = new SessaoVotacao();
        sessaoVotacao.setPauta(pauta);
        sessaoVotacao.setTempoAbertura(tempoAbertura);
        sessaoVotacao.setDuracaoSegundos(duracaoSegundos);
        sessaoVotacao.setFechaAbertura(tempoAbertura.plusSeconds(duracaoSegundos));

        return sessaoVotacao;
    }

    public SessaoVotacaoResponseDTO toResponseDTO(SessaoVotacao sessaoVotacao) {
        return new SessaoVotacaoResponseDTO(
                sessaoVotacao.getId(),
                sessaoVotacao.getTempoAbertura(),
                sessaoVotacao.getFechaAbertura()
        );
    }

    private Long obterDuracaoSegundos(SessaoVotacaoRequestDTO dto) {
        if (dto == null || dto.duracaoSegundos() == null) {
            return DURACAO_PADRAO_SEGUNDOS;
        }

        return dto.duracaoSegundos();
    }
}
