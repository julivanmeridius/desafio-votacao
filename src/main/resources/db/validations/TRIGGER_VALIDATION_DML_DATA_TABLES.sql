-- (A) Trigger que preenche fecha_abertura
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


-- (B) Trigger que valida encerrada_em vs tempo_abertura/fecha_abertura
CREATE OR REPLACE FUNCTION sessao_votacao_validate_encerrada_em()
    RETURNS TRIGGER AS $$
BEGIN
    IF NEW.encerrada_em IS NOT NULL THEN
        IF NEW.encerrada_em < NEW.tempo_abertura THEN
            RAISE EXCEPTION 'encerrada_em (%) não pode ser anterior a tempo_abertura (%)',
                NEW.encerrada_em, NEW.tempo_abertura;
        END IF;

        -- fecha_abertura deve estar preenchida pelo trigger anterior
        IF NEW.fecha_abertura IS NOT NULL AND NEW.encerrada_em > NEW.fecha_abertura THEN
            RAISE EXCEPTION 'encerrada_em (%) não pode ser posterior a fecha_abertura (%)',
                NEW.encerrada_em, NEW.fecha_abertura;
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_sessao_votacao_validate_encerrada_em ON sessao_votacao;

CREATE TRIGGER trg_sessao_votacao_validate_encerrada_em
    BEFORE INSERT OR UPDATE OF tempo_abertura, duracao_segundos, encerrada_em
    ON sessao_votacao
    FOR EACH ROW
EXECUTE FUNCTION sessao_votacao_validate_encerrada_em();


-- (C) Trigger que valida regras de inserção de voto
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

    -- Consistência pauta_id x sessão
    IF NEW.pauta_id <> s.pauta_id THEN
        RAISE EXCEPTION 'pauta_id (%) não corresponde à sessao_votacao.pauta_id (%)',
            NEW.pauta_id, s.pauta_id;
    END IF;

    -- Sessão deve estar aberta
    IF s.encerrada_em IS NOT NULL THEN
        RAISE EXCEPTION 'Sessão % já encerrada em %', NEW.sessao_votacao_id, s.encerrada_em;
    END IF;

    IF s.fecha_abertura IS NOT NULL AND now() >= s.fecha_abertura THEN
        RAISE EXCEPTION 'Sessão % encerrada por horário (fecha_abertura=%)', NEW.sessao_votacao_id, s.fecha_abertura;
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
