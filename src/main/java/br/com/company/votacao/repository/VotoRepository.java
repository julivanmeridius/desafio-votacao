package br.com.company.votacao.repository;

import br.com.company.votacao.model.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {

    boolean existsByPautaIdAndAssociadoId(Long pautaId, Long associadoId);

    long countByPautaIdAndVoto(Long pautaId, String voto);

    @Query("""
            SELECT
                COALESCE(SUM(CASE WHEN v.voto = 'Sim' THEN 1 ELSE 0 END), 0) AS simCount,
                COALESCE(SUM(CASE WHEN v.voto = 'Não' THEN 1 ELSE 0 END), 0) AS naoCount
            FROM Voto v
            WHERE v.pauta.id = :pautaId
            """)
    ResultadoVotacaoProjection countResultadoByPautaId(@Param("pautaId") Long pautaId);

    interface ResultadoVotacaoProjection {
        long getSimCount();
        long getNaoCount();
    }
}
