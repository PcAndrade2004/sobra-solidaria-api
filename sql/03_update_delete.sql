-- =====================================================================
-- Sobra Solidaria (solucao CitizenTech) - 03 - UPDATE e DELETE (sempre com WHERE)
-- Execute depois do script 02. Ao final, o banco fica com 60 registros.
-- =====================================================================

-- U1) Corrige doacoes sem saldo que ainda constavam como DISPONIVEL (afeta a doacao 12)
UPDATE tb_doacao
   SET st_doacao = 'ESGOTADA'
 WHERE qt_disponivel = 0
   AND st_doacao = 'DISPONIVEL';

-- U2) Atualiza o numero de pessoas atendidas da instituicao 3 (Abrigo Esperanca)
UPDATE tb_instituicao
   SET qt_pessoas_atendidas = qt_pessoas_atendidas + 30
 WHERE cd_instituicao = 3;

-- D1) Remove a doacao cadastrada em duplicidade (doacao 15, sem retiradas)
DELETE FROM tb_doacao
 WHERE cd_doacao = 15
   AND NOT EXISTS (SELECT 1 FROM tb_retirada r WHERE r.cd_doacao = tb_doacao.cd_doacao);

-- D2) A Casa do Idoso (instituicao 5) deixou de aceitar Laticinios (categoria 4)
DELETE FROM tb_instituicao_categoria
 WHERE cd_instituicao = 5
   AND cd_categoria = 4;

-- Conferencia: situacao apos as alteracoes
SELECT cd_doacao, nm_alimento, qt_disponivel, st_doacao FROM tb_doacao WHERE cd_doacao IN (12, 15);
SELECT cd_instituicao, nm_instituicao, qt_pessoas_atendidas FROM tb_instituicao WHERE cd_instituicao = 3;

COMMIT;
