-- =====================================================================
-- Sobra Solidaria (solucao CitizenTech) - 04 - 10 consultas numeradas
-- Data de referencia dos exemplos: 10/11/2026 (DATE '2026-11-10').
-- Em uso real, troque por TRUNC(SYSDATE).
-- =====================================================================

-- Q1 (INNER JOIN, WHERE, ORDER BY)
-- Pergunta: quais doacoes ainda disponiveis e dentro da validade devem ser retiradas primeiro?
SELECT d.cd_doacao,
       d.nm_alimento,
       c.nm_categoria,
       e.nm_estabelecimento,
       d.qt_disponivel,
       d.ds_unidade,
       d.dt_validade
  FROM tb_doacao d
 INNER JOIN tb_estabelecimento e ON e.cd_estabelecimento = d.cd_estabelecimento
 INNER JOIN tb_categoria c ON c.cd_categoria = d.cd_categoria
 WHERE d.st_doacao = 'DISPONIVEL'
   AND d.dt_validade >= DATE '2026-11-10'
 ORDER BY d.dt_validade, d.nm_alimento;

-- Q2 (INNER JOIN, calculo no SELECT, ORDER BY)
-- Pergunta: quantas refeicoes cada retirada gerou? (estimativa: 2 refeicoes por unidade retirada)
SELECT r.cd_retirada,
       i.nm_instituicao,
       d.nm_alimento,
       r.qt_retirada,
       r.qt_retirada * 2 AS refeicoes_estimadas
  FROM tb_retirada r
 INNER JOIN tb_instituicao i ON i.cd_instituicao = r.cd_instituicao
 INNER JOIN tb_doacao d ON d.cd_doacao = r.cd_doacao
 ORDER BY refeicoes_estimadas DESC, r.cd_retirada;

-- Q3 (COUNT, SUM, GROUP BY, ORDER BY)
-- Pergunta: quanto alimento esta disponivel em cada categoria?
SELECT c.nm_categoria,
       COUNT(d.cd_doacao)     AS qtd_doacoes,
       SUM(d.qt_disponivel)   AS total_disponivel
  FROM tb_doacao d
 INNER JOIN tb_categoria c ON c.cd_categoria = d.cd_categoria
 WHERE d.st_doacao = 'DISPONIVEL'
 GROUP BY c.nm_categoria
 ORDER BY total_disponivel DESC;

-- Q4 (AVG, MAX, MIN, GROUP BY, HAVING)
-- Pergunta: quais estabelecimentos doam com regularidade (2 ou mais doacoes) e qual o perfil dos lotes?
SELECT e.nm_estabelecimento,
       COUNT(*)                        AS qtd_doacoes,
       ROUND(AVG(d.qt_disponivel), 2)  AS media_lote,
       MAX(d.qt_disponivel)            AS maior_lote,
       MIN(d.qt_disponivel)            AS menor_lote
  FROM tb_doacao d
 INNER JOIN tb_estabelecimento e ON e.cd_estabelecimento = d.cd_estabelecimento
 GROUP BY e.nm_estabelecimento
HAVING COUNT(*) >= 2
 ORDER BY qtd_doacoes DESC, e.nm_estabelecimento;

-- Q5 (SUM, GROUP BY, HAVING)
-- Pergunta: quais instituicoes mais retiraram alimento (total retirado de 12 ou mais)?
SELECT i.nm_instituicao,
       COUNT(r.cd_retirada) AS qtd_retiradas,
       SUM(r.qt_retirada)   AS total_retirado
  FROM tb_retirada r
 INNER JOIN tb_instituicao i ON i.cd_instituicao = r.cd_instituicao
 GROUP BY i.nm_instituicao
HAVING SUM(r.qt_retirada) >= 12
 ORDER BY total_retirado DESC;

-- Q6 (LEFT JOIN, COUNT)
-- Pergunta: quais estabelecimentos ainda nao fizeram nenhuma doacao (inclui os com zero)?
SELECT e.nm_estabelecimento,
       COUNT(d.cd_doacao) AS qtd_doacoes
  FROM tb_estabelecimento e
  LEFT JOIN tb_doacao d ON d.cd_estabelecimento = e.cd_estabelecimento
 GROUP BY e.nm_estabelecimento
 ORDER BY qtd_doacoes, e.nm_estabelecimento;

-- Q7 (RIGHT JOIN, COUNT)
-- Pergunta: quais categorias nao receberam doacoes (inclui as com zero)?
SELECT c.nm_categoria,
       COUNT(d.cd_doacao) AS qtd_doacoes
  FROM tb_doacao d
 RIGHT JOIN tb_categoria c ON c.cd_categoria = d.cd_categoria
 GROUP BY c.nm_categoria
 ORDER BY qtd_doacoes, c.nm_categoria;

-- Q8 (SUBSELECT, WHERE)
-- Pergunta: quais doacoes disponiveis tem quantidade acima da media das doacoes disponiveis?
SELECT d.cd_doacao,
       d.nm_alimento,
       d.qt_disponivel,
       d.ds_unidade
  FROM tb_doacao d
 WHERE d.st_doacao = 'DISPONIVEL'
   AND d.qt_disponivel > (SELECT AVG(x.qt_disponivel)
                            FROM tb_doacao x
                           WHERE x.st_doacao = 'DISPONIVEL')
 ORDER BY d.qt_disponivel DESC;

-- Q9 (INNER JOIN com a associativa N:N, WHERE, ORDER BY)
-- Pergunta: para cada doacao disponivel, quais instituicoes aceitam aquela categoria (match doacao x instituicao)?
SELECT d.cd_doacao,
       d.nm_alimento,
       c.nm_categoria,
       i.nm_instituicao,
       i.qt_pessoas_atendidas
  FROM tb_doacao d
 INNER JOIN tb_categoria c ON c.cd_categoria = d.cd_categoria
 INNER JOIN tb_instituicao_categoria ic ON ic.cd_categoria = c.cd_categoria
 INNER JOIN tb_instituicao i ON i.cd_instituicao = ic.cd_instituicao
 WHERE d.st_doacao = 'DISPONIVEL'
   AND d.dt_validade >= DATE '2026-11-10'
 ORDER BY d.dt_validade, d.cd_doacao, i.qt_pessoas_atendidas DESC;

-- Q10 (SUBSELECT, SUM, GROUP BY, ORDER BY, calculo)
-- Pergunta: quanto alimento esta vencido e sem retirada, e quanto isso representa do total disponivel de cada estabelecimento?
SELECT e.nm_estabelecimento,
       SUM(d.qt_disponivel) AS qtd_vencida,
       ROUND(SUM(d.qt_disponivel) * 100 /
             (SELECT SUM(t.qt_disponivel)
                FROM tb_doacao t
               WHERE t.cd_estabelecimento = e.cd_estabelecimento), 1) AS pct_do_estabelecimento
  FROM tb_doacao d
 INNER JOIN tb_estabelecimento e ON e.cd_estabelecimento = d.cd_estabelecimento
 WHERE d.st_doacao = 'DISPONIVEL'
   AND d.dt_validade < DATE '2026-11-10'
 GROUP BY e.cd_estabelecimento, e.nm_estabelecimento
 ORDER BY qtd_vencida DESC;
