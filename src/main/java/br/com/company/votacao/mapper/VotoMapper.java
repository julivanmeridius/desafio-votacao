package br.com.company.votacao.mapper;

import br.com.company.votacao.dto.VotoRequestDTO;
import br.com.company.votacao.dto.VotoResponseDTO;
import br.com.company.votacao.model.Associado;
import br.com.company.votacao.model.Pauta;
import br.com.company.votacao.model.SessaoVotacao;
import br.com.company.votacao.model.Voto;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class VotoMapper {

    public Voto toEntity(Pauta pauta, SessaoVotacao sessaoVotacao, Associado associado, VotoRequestDTO dto) {
        var voto = new Voto();
        voto.setPauta(pauta);
        voto.setSessaoVotacao(sessaoVotacao);
        voto.setAssociado(associado);
        voto.setVoto(dto.voto());
        voto.setRecebidoEm(OffsetDateTime.now());
        return voto;
    }

    public VotoResponseDTO toResponseDTO(Voto voto) {
        return new VotoResponseDTO(voto.getId(), voto.getRecebidoEm());
    }
}
