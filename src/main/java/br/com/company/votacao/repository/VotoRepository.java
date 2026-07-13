package br.com.company.votacao.repository;

import br.com.company.votacao.model.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {

    boolean existsByPautaIdAndAssociadoId(Long pautaId, Long associadoId);

    long countByPautaIdAndVoto(Long pautaId, String voto);
}
