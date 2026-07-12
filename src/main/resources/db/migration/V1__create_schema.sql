CREATE SCHEMA IF NOT EXISTS public;

CREATE TABLE IF NOT EXISTS pauta (
    id              BIGSERIAL PRIMARY KEY,
    titulo          VARCHAR(200) NOT NULL,
    descricao       TEXT,
    criado_em       TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS associado (
    id              BIGSERIAL PRIMARY KEY,
    nome            VARCHAR(200) NOT NULL,
    criado_em       TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS sessao_votacao (
    id                 BIGSERIAL PRIMARY KEY,
    pauta_id           BIGINT NOT NULL REFERENCES pauta(id) ON DELETE CASCADE,
    tempo_abertura     TIMESTAMPTZ NOT NULL DEFAULT now(),
    duracao_segundos   BIGINT NOT NULL DEFAULT 60,
    fecha_abertura     TIMESTAMPTZ,
    encerrada_em       TIMESTAMPTZ,
    criado_em          TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT sessao_votacao_duracao_pos CHECK (duracao_segundos > 0)
);

CREATE OR REPLACE FUNCTION sessao_votacao_set_fecha_abertura()
RETURNS TRIGGER AS $$
BEGIN
  NEW.fecha_abertura := NEW.tempo_abertura
                         + (NEW.duracao_segundos::text || ' seconds')::interval;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_sessao_votacao_set_fecha_abertura ON sessao_votacao;

CREATE TRIGGER trg_sessao_votacao_set_fecha_abertura
    BEFORE INSERT OR UPDATE OF tempo_abertura, duracao_segundos
    ON sessao_votacao
    FOR EACH ROW
    EXECUTE FUNCTION sessao_votacao_set_fecha_abertura();

CREATE INDEX IF NOT EXISTS idx_sessao_votacao_pauta_tempo
    ON sessao_votacao(pauta_id, tempo_abertura);

CREATE TABLE IF NOT EXISTS voto (
    id                 BIGSERIAL PRIMARY KEY,
    pauta_id           BIGINT NOT NULL REFERENCES pauta(id) ON DELETE CASCADE,
    sessao_votacao_id  BIGINT NOT NULL REFERENCES sessao_votacao(id) ON DELETE CASCADE,
    associado_id       BIGINT NOT NULL REFERENCES associado(id) ON DELETE RESTRICT,
    voto               VARCHAR(3) NOT NULL,
    recebido_em        TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT voto_check
        CHECK (voto IN ('Sim', 'Não')),

    CONSTRAINT voto_unico_por_associado_por_pauta
        UNIQUE (pauta_id, associado_id)
);

CREATE INDEX IF NOT EXISTS idx_voto_sessao_votacao ON voto(sessao_votacao_id);

CREATE OR REPLACE FUNCTION voto_validate_sessao_aberta_e_pauta()
RETURNS TRIGGER AS $$
DECLARE
    s RECORD;
BEGIN
    SELECT pauta_id, fecha_abertura, encerrada_em
    INTO s
    FROM sessao_votacao
    WHERE id = NEW.sessao_votacao_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'sessao_votacao_id % não existe', NEW.sessao_votacao_id;
    END IF;

    IF NEW.pauta_id <> s.pauta_id THEN
        RAISE EXCEPTION 'pauta_id (%) não corresponde à sessao_votacao.pauta_id (%)',
            NEW.pauta_id, s.pauta_id;
    END IF;

    IF s.encerrada_em IS NOT NULL THEN
        RAISE EXCEPTION 'Sessão % já encerrada em %', NEW.sessao_votacao_id, s.encerrada_em;
    END IF;

    IF s.fecha_abertura IS NOT NULL AND now() >= s.fecha_abertura THEN
        RAISE EXCEPTION 'Sessão % encerrada por horário (fecha_abertura=%)',
            NEW.sessao_votacao_id, s.fecha_abertura;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_voto_validate_sessao_aberta_e_pauta ON voto;

CREATE TRIGGER trg_voto_validate_sessao_aberta_e_pauta
    BEFORE INSERT OR UPDATE OF pauta_id, sessao_votacao_id
    ON voto
    FOR EACH ROW
    EXECUTE FUNCTION voto_validate_sessao_aberta_e_pauta();
