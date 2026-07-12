WITH
    p1 AS (
        INSERT INTO pauta (titulo, descricao) VALUES
                                                  ('Pauta A', 'Descrição A')
            RETURNING id
    ),
    p2 AS (
        INSERT INTO pauta (titulo, descricao) VALUES
                                                  ('Pauta B', 'Descrição B')
            RETURNING id
    ),
    a1 AS (
        INSERT INTO associado (nome) VALUES ('Ana') RETURNING id
    ),
    a2 AS (
        INSERT INTO associado (nome) VALUES ('Bruno') RETURNING id
    ),
    a3 AS (
        INSERT INTO associado (nome) VALUES ('Carla') RETURNING id
    ),
    s1 AS (
        INSERT INTO sessao_votacao (pauta_id, tempo_abertura, duracao_segundos, encerrada_em)
            SELECT p1.id, now() - interval '1 minute', 300, NULL
            FROM p1
            RETURNING id
    ),
    s2 AS (
        INSERT INTO sessao_votacao (pauta_id, tempo_abertura, duracao_segundos, encerrada_em)
            SELECT p2.id, now() - interval '1 minute', 300, NULL
            FROM p2
            RETURNING id
    )
INSERT INTO voto (pauta_id, sessao_votacao_id, associado_id, voto)
SELECT
    (SELECT id FROM p1), (SELECT id FROM s1), (SELECT id FROM a1), 'Sim'
UNION ALL
SELECT
    (SELECT id FROM p1), (SELECT id FROM s1), (SELECT id FROM a2), 'Não'
UNION ALL
SELECT
    (SELECT id FROM p2), (SELECT id FROM s2), (SELECT id FROM a3), 'Sim';
