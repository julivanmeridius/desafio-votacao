-- Drop tables (child first due to FK dependencies)
DROP TABLE IF EXISTS voto;
DROP TABLE IF EXISTS sessao_votacao;
DROP TABLE IF EXISTS associado;
DROP TABLE IF EXISTS pauta;

-- Drop trigger + functions
DROP TRIGGER IF EXISTS trg_sessao_votacao_set_fecha_abertura ON sessao_votacao;

DROP FUNCTION IF EXISTS sessao_votacao_set_fecha_abertura();
