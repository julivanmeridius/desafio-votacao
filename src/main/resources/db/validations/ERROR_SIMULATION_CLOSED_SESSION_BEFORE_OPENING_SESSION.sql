DO $$
BEGIN
  INSERT INTO sessao_votacao (pauta_id, tempo_abertura, duracao_segundos, encerrada_em)
  SELECT
    (SELECT id FROM pauta WHERE titulo = 'Pauta A'),
    now(),
    300,
    now() - interval '1 minute';
EXCEPTION WHEN others THEN
  RAISE NOTICE 'OK (erro esperado): %', SQLERRM;
END $$;
