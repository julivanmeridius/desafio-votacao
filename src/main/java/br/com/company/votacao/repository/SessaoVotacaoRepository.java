package br.com.company.votacao.repository;

import br.com.company.votacao.model.SessaoVotacao;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static br.com.company.votacao.constants.VotacaoConstants.PARAM_PAUTA_ID;

@Repository
public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {

    @Query("""
            SELECT s
            FROM SessaoVotacao s
            WHERE s.pauta.id = :pautaId AND s.encerradaEm IS NULL
            AND s.fechaAbertura > CURRENT_TIMESTAMP
            """)
    Optional<SessaoVotacao> findActivaByPautaId(@Param(PARAM_PAUTA_ID) Long pautaId);

    //--Usando lock pessimita para evitar que duas threads/processos encerrem a mesma sessão ao mesmo tempo.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT s
            FROM SessaoVotacao s
            JOIN FETCH s.pauta
            WHERE s.encerradaEm IS NULL
              AND s.fechaAbertura <= CURRENT_TIMESTAMP
            """)
    List<SessaoVotacao> findSessoesExpiradasParaEncerrar();
}
