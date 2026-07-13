package br.com.company.votacao.repository;

import br.com.company.votacao.model.SessaoVotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {

    @Query("SELECT s FROM SessaoVotacao s WHERE s.pauta.id = :pautaId AND s.encerradaEm IS NULL AND s.fechaAbertura > CURRENT_TIMESTAMP")
    Optional<SessaoVotacao> findActivaByPautaId(@Param("pautaId") Long pautaId);
}
