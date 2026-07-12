DO $$
DECLARE
  sid BIGINT;
  pid_mismatch BIGINT;
  aid BIGINT;
  pid_sessao BIGINT;
BEGIN
  pid_mismatch := (SELECT id FROM pauta WHERE titulo = 'Pauta A'); -- vai estar errado
  aid := (SELECT id FROM associado WHERE nome = 'Bruno');
  pid_sessao := (SELECT id FROM pauta WHERE titulo = 'Pauta B');

  INSERT INTO sessao_votacao (pauta_id, tempo_abertura, duracao_segundos, encerrada_em)
  VALUES (pid_sessao, now() - interval '1 minute', 300, NULL)
  RETURNING id INTO sid;

  -- ERRO: tenta usar pauta_id A com sessão da pauta B
  INSERT INTO voto (pauta_id, sessao_votacao_id, associado_id, voto)
  VALUES (pid_mismatch, sid, aid, 'Não');

EXCEPTION WHEN others THEN
  RAISE NOTICE 'OK (erro esperado): %', SQLERRM;
END $$;
