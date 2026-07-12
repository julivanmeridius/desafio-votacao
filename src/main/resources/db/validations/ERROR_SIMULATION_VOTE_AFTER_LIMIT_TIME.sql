DO $$
DECLARE
  sid BIGINT;
  pid BIGINT;
  aid BIGINT;
BEGIN
  pid := (SELECT id FROM pauta WHERE titulo = 'Pauta A');
  aid := (SELECT id FROM associado WHERE nome = 'Ana');

  INSERT INTO sessao_votacao (pauta_id, tempo_abertura, duracao_segundos, encerrada_em)
  VALUES (pid, now() - interval '10 minutes', 300, NULL)
  RETURNING id INTO sid;

  INSERT INTO voto (pauta_id, sessao_votacao_id, associado_id, voto)
  VALUES (pid, sid, aid, 'Sim');

EXCEPTION WHEN others THEN
  RAISE NOTICE 'OK (erro esperado): %', SQLERRM;
END $$;
