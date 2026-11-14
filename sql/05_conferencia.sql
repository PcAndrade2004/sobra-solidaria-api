-- Conferencia da contagem de registros (esperado apos os scripts 01 a 03: 60 no total)
SELECT 'tb_estabelecimento' AS tabela, COUNT(*) AS total FROM tb_estabelecimento
UNION ALL SELECT 'tb_categoria', COUNT(*) FROM tb_categoria
UNION ALL SELECT 'tb_instituicao', COUNT(*) FROM tb_instituicao
UNION ALL SELECT 'tb_instituicao_categoria', COUNT(*) FROM tb_instituicao_categoria
UNION ALL SELECT 'tb_doacao', COUNT(*) FROM tb_doacao
UNION ALL SELECT 'tb_retirada', COUNT(*) FROM tb_retirada;
