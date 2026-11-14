-- =====================================================================
-- Sobra Solidaria (solucao CitizenTech) - 02 - Insercao de dados
-- 62 registros inseridos; apos os 2 DELETEs do script 03 ficam 60 no total.
-- Os ids vem das sequences (1, 2, 3...), por isso execute este script UMA vez,
-- logo apos o script 01. Textos sem acento para evitar problemas de codificacao.
-- =====================================================================

-- ---------------------------------------------------------------------
-- tb_estabelecimento (8 registros)
-- ---------------------------------------------------------------------
INSERT INTO tb_estabelecimento (cd_estabelecimento, nm_estabelecimento, nr_cnpj, ds_email, ds_endereco, dt_cadastro)
VALUES (sq_tb_estabelecimento.NEXTVAL, 'Padaria Pao Quente', '11111111000101', 'contato@paoquente.com.br', 'Rua das Acacias, 120 - Cotia/SP', TIMESTAMP '2026-10-01 08:00:00');
INSERT INTO tb_estabelecimento (cd_estabelecimento, nm_estabelecimento, nr_cnpj, ds_email, ds_endereco, dt_cadastro)
VALUES (sq_tb_estabelecimento.NEXTVAL, 'Supermercado Bom Preco', '22222222000102', 'doacoes@bompreco.com.br', 'Av. Brasil, 1500 - Sao Paulo/SP', TIMESTAMP '2026-10-02 09:15:00');
INSERT INTO tb_estabelecimento (cd_estabelecimento, nm_estabelecimento, nr_cnpj, ds_email, ds_endereco, dt_cadastro)
VALUES (sq_tb_estabelecimento.NEXTVAL, 'Restaurante Sabor da Terra', '33333333000103', 'gerencia@sabordaterra.com.br', 'Rua do Comercio, 45 - Osasco/SP', TIMESTAMP '2026-10-03 10:30:00');
INSERT INTO tb_estabelecimento (cd_estabelecimento, nm_estabelecimento, nr_cnpj, ds_email, ds_endereco, dt_cadastro)
VALUES (sq_tb_estabelecimento.NEXTVAL, 'Hortifruti Verde Vivo', '44444444000104', 'vendas@verdevivo.com.br', 'Rua das Flores, 300 - Cotia/SP', TIMESTAMP '2026-10-04 07:45:00');
INSERT INTO tb_estabelecimento (cd_estabelecimento, nm_estabelecimento, nr_cnpj, ds_email, ds_endereco, dt_cadastro)
VALUES (sq_tb_estabelecimento.NEXTVAL, 'Mercado Nova Era', '55555555000105', 'contato@novaera.com.br', 'Av. Paulista, 900 - Sao Paulo/SP', TIMESTAMP '2026-10-05 11:00:00');
INSERT INTO tb_estabelecimento (cd_estabelecimento, nm_estabelecimento, nr_cnpj, ds_email, ds_endereco, dt_cadastro)
VALUES (sq_tb_estabelecimento.NEXTVAL, 'Padaria Doce Trigo', '66666666000106', 'padaria@docetrigo.com.br', 'Rua Sete de Setembro, 78 - Barueri/SP', TIMESTAMP '2026-10-06 06:30:00');
INSERT INTO tb_estabelecimento (cd_estabelecimento, nm_estabelecimento, nr_cnpj, ds_email, ds_endereco, dt_cadastro)
VALUES (sq_tb_estabelecimento.NEXTVAL, 'Cantina Italiana', '77777777000107', 'cantina@italiana.com.br', 'Rua Augusta, 210 - Sao Paulo/SP', TIMESTAMP '2026-10-07 12:00:00');
INSERT INTO tb_estabelecimento (cd_estabelecimento, nm_estabelecimento, nr_cnpj, ds_email, ds_endereco, dt_cadastro)
VALUES (sq_tb_estabelecimento.NEXTVAL, 'Emporio Grao e Cia', '88888888000108', 'emporio@graoecia.com.br', 'Rua XV de Novembro, 15 - Carapicuiba/SP', TIMESTAMP '2026-10-08 14:20:00');

-- ---------------------------------------------------------------------
-- tb_categoria (6 registros)
-- ---------------------------------------------------------------------
INSERT INTO tb_categoria (cd_categoria, nm_categoria, ds_categoria, fl_perecivel)
VALUES (sq_tb_categoria.NEXTVAL, 'Paes e Massas', 'Paes, bolos e massas de padaria', 'S');
INSERT INTO tb_categoria (cd_categoria, nm_categoria, ds_categoria, fl_perecivel)
VALUES (sq_tb_categoria.NEXTVAL, 'Frutas e Verduras', 'Hortifruti fresco', 'S');
INSERT INTO tb_categoria (cd_categoria, nm_categoria, ds_categoria, fl_perecivel)
VALUES (sq_tb_categoria.NEXTVAL, 'Refeicoes Prontas', 'Marmitas e pratos preparados', 'S');
INSERT INTO tb_categoria (cd_categoria, nm_categoria, ds_categoria, fl_perecivel)
VALUES (sq_tb_categoria.NEXTVAL, 'Laticinios', 'Leite, iogurtes e queijos', 'S');
INSERT INTO tb_categoria (cd_categoria, nm_categoria, ds_categoria, fl_perecivel)
VALUES (sq_tb_categoria.NEXTVAL, 'Graos e Cereais', 'Arroz, feijao e similares', 'N');
INSERT INTO tb_categoria (cd_categoria, nm_categoria, ds_categoria, fl_perecivel)
VALUES (sq_tb_categoria.NEXTVAL, 'Bebidas', 'Sucos e bebidas embaladas', 'N');

-- ---------------------------------------------------------------------
-- tb_instituicao (8 registros)
-- ---------------------------------------------------------------------
INSERT INTO tb_instituicao (cd_instituicao, nm_instituicao, nr_cnpj, ds_email, ds_endereco, qt_pessoas_atendidas, dt_cadastro)
VALUES (sq_tb_instituicao.NEXTVAL, 'Banco de Alimentos Zona Sul', '91111111000101', 'contato@bancozonasul.org.br', 'Rua da Esperanca, 50 - Sao Paulo/SP', 450, TIMESTAMP '2026-10-01 10:00:00');
INSERT INTO tb_instituicao (cd_instituicao, nm_instituicao, nr_cnpj, ds_email, ds_endereco, qt_pessoas_atendidas, dt_cadastro)
VALUES (sq_tb_instituicao.NEXTVAL, 'ONG Prato Cheio', '92222222000102', 'ola@pratocheio.org.br', 'Av. Solidariedade, 800 - Cotia/SP', 300, TIMESTAMP '2026-10-02 10:30:00');
INSERT INTO tb_instituicao (cd_instituicao, nm_instituicao, nr_cnpj, ds_email, ds_endereco, qt_pessoas_atendidas, dt_cadastro)
VALUES (sq_tb_instituicao.NEXTVAL, 'Abrigo Esperanca', '93333333000103', 'abrigo@esperanca.org.br', 'Rua Nova, 12 - Osasco/SP', 120, TIMESTAMP '2026-10-03 11:00:00');
INSERT INTO tb_instituicao (cd_instituicao, nm_instituicao, nr_cnpj, ds_email, ds_endereco, qt_pessoas_atendidas, dt_cadastro)
VALUES (sq_tb_instituicao.NEXTVAL, 'Cozinha Comunitaria Vila Nova', '94444444000104', 'cozinha@vilanova.org.br', 'Rua do Mutirao, 99 - Sao Paulo/SP', 600, TIMESTAMP '2026-10-04 09:00:00');
INSERT INTO tb_instituicao (cd_instituicao, nm_instituicao, nr_cnpj, ds_email, ds_endereco, qt_pessoas_atendidas, dt_cadastro)
VALUES (sq_tb_instituicao.NEXTVAL, 'Casa do Idoso Sao Jose', '95555555000105', 'casa@saojose.org.br', 'Rua das Palmeiras, 33 - Barueri/SP', 80, TIMESTAMP '2026-10-05 15:00:00');
INSERT INTO tb_instituicao (cd_instituicao, nm_instituicao, nr_cnpj, ds_email, ds_endereco, qt_pessoas_atendidas, dt_cadastro)
VALUES (sq_tb_instituicao.NEXTVAL, 'Instituto Mesa Farta', '96666666000106', 'instituto@mesafarta.org.br', 'Av. Esperanca, 410 - Sao Paulo/SP', 350, TIMESTAMP '2026-10-06 13:30:00');
INSERT INTO tb_instituicao (cd_instituicao, nm_instituicao, nr_cnpj, ds_email, ds_endereco, qt_pessoas_atendidas, dt_cadastro)
VALUES (sq_tb_instituicao.NEXTVAL, 'Associacao Moradores Jardim Azul', '97777777000107', 'moradores@jardimazul.org.br', 'Rua Azul, 7 - Cotia/SP', 200, TIMESTAMP '2026-10-07 16:00:00');
INSERT INTO tb_instituicao (cd_instituicao, nm_instituicao, nr_cnpj, ds_email, ds_endereco, qt_pessoas_atendidas, dt_cadastro)
VALUES (sq_tb_instituicao.NEXTVAL, 'Creche Pequenos Passos', '98888888000108', 'creche@pequenospassos.org.br', 'Rua da Alegria, 21 - Carapicuiba/SP', 90, TIMESTAMP '2026-10-08 08:30:00');

-- ---------------------------------------------------------------------
-- tb_instituicao_categoria - associativa N:N (14 registros)
-- ---------------------------------------------------------------------
INSERT INTO tb_instituicao_categoria (cd_instituicao, cd_categoria, dt_vinculo) VALUES (1, 1, TIMESTAMP '2026-10-10 10:00:00');
INSERT INTO tb_instituicao_categoria (cd_instituicao, cd_categoria, dt_vinculo) VALUES (1, 2, TIMESTAMP '2026-10-10 10:01:00');
INSERT INTO tb_instituicao_categoria (cd_instituicao, cd_categoria, dt_vinculo) VALUES (1, 5, TIMESTAMP '2026-10-10 10:02:00');
INSERT INTO tb_instituicao_categoria (cd_instituicao, cd_categoria, dt_vinculo) VALUES (2, 2, TIMESTAMP '2026-10-10 10:03:00');
INSERT INTO tb_instituicao_categoria (cd_instituicao, cd_categoria, dt_vinculo) VALUES (2, 3, TIMESTAMP '2026-10-10 10:04:00');
INSERT INTO tb_instituicao_categoria (cd_instituicao, cd_categoria, dt_vinculo) VALUES (2, 5, TIMESTAMP '2026-10-10 10:05:00');
INSERT INTO tb_instituicao_categoria (cd_instituicao, cd_categoria, dt_vinculo) VALUES (3, 1, TIMESTAMP '2026-10-11 09:00:00');
INSERT INTO tb_instituicao_categoria (cd_instituicao, cd_categoria, dt_vinculo) VALUES (3, 3, TIMESTAMP '2026-10-11 09:01:00');
INSERT INTO tb_instituicao_categoria (cd_instituicao, cd_categoria, dt_vinculo) VALUES (4, 2, TIMESTAMP '2026-10-11 09:02:00');
INSERT INTO tb_instituicao_categoria (cd_instituicao, cd_categoria, dt_vinculo) VALUES (4, 3, TIMESTAMP '2026-10-11 09:03:00');
INSERT INTO tb_instituicao_categoria (cd_instituicao, cd_categoria, dt_vinculo) VALUES (4, 4, TIMESTAMP '2026-10-11 09:04:00');
INSERT INTO tb_instituicao_categoria (cd_instituicao, cd_categoria, dt_vinculo) VALUES (5, 1, TIMESTAMP '2026-10-12 14:00:00');
INSERT INTO tb_instituicao_categoria (cd_instituicao, cd_categoria, dt_vinculo) VALUES (5, 4, TIMESTAMP '2026-10-12 14:01:00');
INSERT INTO tb_instituicao_categoria (cd_instituicao, cd_categoria, dt_vinculo) VALUES (6, 5, TIMESTAMP '2026-10-12 14:02:00');

-- ---------------------------------------------------------------------
-- tb_doacao (15 registros; o ultimo e um cadastro duplicado, removido no script 03)
-- cd_doacao 12 entra com quantidade 0 e status DISPONIVEL (inconsistencia corrigida no UPDATE do script 03)
-- ---------------------------------------------------------------------
INSERT INTO tb_doacao (cd_doacao, cd_estabelecimento, cd_categoria, nm_alimento, ds_alimento, qt_disponivel, ds_unidade, dt_validade, st_doacao, dt_cadastro)
VALUES (sq_tb_doacao.NEXTVAL, 1, 1, 'Pao frances', 'Sobra do dia, embalado', 12.50, 'kg', DATE '2026-11-12', 'DISPONIVEL', TIMESTAMP '2026-11-08 18:00:00');
INSERT INTO tb_doacao (cd_doacao, cd_estabelecimento, cd_categoria, nm_alimento, ds_alimento, qt_disponivel, ds_unidade, dt_validade, st_doacao, dt_cadastro)
VALUES (sq_tb_doacao.NEXTVAL, 1, 1, 'Bolo simples', 'Bolo de fuba em fatias', 3.00, 'kg', DATE '2026-11-11', 'DISPONIVEL', TIMESTAMP '2026-11-08 18:10:00');
INSERT INTO tb_doacao (cd_doacao, cd_estabelecimento, cd_categoria, nm_alimento, ds_alimento, qt_disponivel, ds_unidade, dt_validade, st_doacao, dt_cadastro)
VALUES (sq_tb_doacao.NEXTVAL, 2, 2, 'Banana prata', 'Caixas com frutas maduras', 25.00, 'kg', DATE '2026-11-14', 'DISPONIVEL', TIMESTAMP '2026-11-08 09:00:00');
INSERT INTO tb_doacao (cd_doacao, cd_estabelecimento, cd_categoria, nm_alimento, ds_alimento, qt_disponivel, ds_unidade, dt_validade, st_doacao, dt_cadastro)
VALUES (sq_tb_doacao.NEXTVAL, 2, 4, 'Iogurte natural', 'Potes de 170 g proximos do vencimento', 50.00, 'un', DATE '2026-11-20', 'DISPONIVEL', TIMESTAMP '2026-11-08 09:10:00');
INSERT INTO tb_doacao (cd_doacao, cd_estabelecimento, cd_categoria, nm_alimento, ds_alimento, qt_disponivel, ds_unidade, dt_validade, st_doacao, dt_cadastro)
VALUES (sq_tb_doacao.NEXTVAL, 3, 3, 'Marmita arroz e feijao', 'Marmitas do almoco', 25.00, 'un', DATE '2026-11-10', 'DISPONIVEL', TIMESTAMP '2026-11-09 15:00:00');
INSERT INTO tb_doacao (cd_doacao, cd_estabelecimento, cd_categoria, nm_alimento, ds_alimento, qt_disponivel, ds_unidade, dt_validade, st_doacao, dt_cadastro)
VALUES (sq_tb_doacao.NEXTVAL, 3, 3, 'Strogonoff de frango', 'Bandejas refrigeradas', 20.00, 'un', DATE '2026-11-09', 'DISPONIVEL', TIMESTAMP '2026-11-07 15:30:00');
INSERT INTO tb_doacao (cd_doacao, cd_estabelecimento, cd_categoria, nm_alimento, ds_alimento, qt_disponivel, ds_unidade, dt_validade, st_doacao, dt_cadastro)
VALUES (sq_tb_doacao.NEXTVAL, 4, 2, 'Tomate', 'Tomates com pequenas manchas, bons para molho', 25.00, 'kg', DATE '2026-11-15', 'DISPONIVEL', TIMESTAMP '2026-11-08 07:30:00');
INSERT INTO tb_doacao (cd_doacao, cd_estabelecimento, cd_categoria, nm_alimento, ds_alimento, qt_disponivel, ds_unidade, dt_validade, st_doacao, dt_cadastro)
VALUES (sq_tb_doacao.NEXTVAL, 4, 2, 'Alface crespa', 'Pes de alface', 0.00, 'un', DATE '2026-11-13', 'ESGOTADA', TIMESTAMP '2026-11-07 07:30:00');
INSERT INTO tb_doacao (cd_doacao, cd_estabelecimento, cd_categoria, nm_alimento, ds_alimento, qt_disponivel, ds_unidade, dt_validade, st_doacao, dt_cadastro)
VALUES (sq_tb_doacao.NEXTVAL, 5, 5, 'Arroz tipo 1', 'Pacotes com embalagem avariada', 30.00, 'kg', DATE '2027-03-01', 'DISPONIVEL', TIMESTAMP '2026-11-05 11:00:00');
INSERT INTO tb_doacao (cd_doacao, cd_estabelecimento, cd_categoria, nm_alimento, ds_alimento, qt_disponivel, ds_unidade, dt_validade, st_doacao, dt_cadastro)
VALUES (sq_tb_doacao.NEXTVAL, 5, 5, 'Feijao carioca', 'Pacotes de 1 kg', 20.00, 'kg', DATE '2027-02-15', 'DISPONIVEL', TIMESTAMP '2026-11-05 11:10:00');
INSERT INTO tb_doacao (cd_doacao, cd_estabelecimento, cd_categoria, nm_alimento, ds_alimento, qt_disponivel, ds_unidade, dt_validade, st_doacao, dt_cadastro)
VALUES (sq_tb_doacao.NEXTVAL, 6, 1, 'Pao de forma', 'Pacotes fechados', 15.00, 'un', DATE '2026-11-08', 'DISPONIVEL', TIMESTAMP '2026-11-06 17:00:00');
INSERT INTO tb_doacao (cd_doacao, cd_estabelecimento, cd_categoria, nm_alimento, ds_alimento, qt_disponivel, ds_unidade, dt_validade, st_doacao, dt_cadastro)
VALUES (sq_tb_doacao.NEXTVAL, 6, 1, 'Rosca doce', 'Roscas com cobertura', 0.00, 'un', DATE '2026-11-12', 'DISPONIVEL', TIMESTAMP '2026-11-08 17:00:00');
INSERT INTO tb_doacao (cd_doacao, cd_estabelecimento, cd_categoria, nm_alimento, ds_alimento, qt_disponivel, ds_unidade, dt_validade, st_doacao, dt_cadastro)
VALUES (sq_tb_doacao.NEXTVAL, 7, 3, 'Lasanha bolonhesa', 'Porcoes individuais congeladas', 12.00, 'kg', DATE '2026-11-11', 'DISPONIVEL', TIMESTAMP '2026-11-09 13:00:00');
INSERT INTO tb_doacao (cd_doacao, cd_estabelecimento, cd_categoria, nm_alimento, ds_alimento, qt_disponivel, ds_unidade, dt_validade, st_doacao, dt_cadastro)
VALUES (sq_tb_doacao.NEXTVAL, 7, 4, 'Queijo mussarela', 'Pecas fatiadas', 6.00, 'kg', DATE '2026-11-25', 'DISPONIVEL', TIMESTAMP '2026-11-09 13:10:00');
INSERT INTO tb_doacao (cd_doacao, cd_estabelecimento, cd_categoria, nm_alimento, ds_alimento, qt_disponivel, ds_unidade, dt_validade, st_doacao, dt_cadastro)
VALUES (sq_tb_doacao.NEXTVAL, 2, 4, 'Iogurte natural', 'Potes de 170 g proximos do vencimento', 50.00, 'un', DATE '2026-11-20', 'DISPONIVEL', TIMESTAMP '2026-11-08 09:12:00');

-- ---------------------------------------------------------------------
-- tb_retirada (11 registros)
-- ---------------------------------------------------------------------
INSERT INTO tb_retirada (cd_retirada, cd_doacao, cd_instituicao, qt_retirada, ds_observacao, dt_retirada)
VALUES (sq_tb_retirada.NEXTVAL, 1, 1, 5.00, 'Retirada as 19h', TIMESTAMP '2026-11-08 19:00:00');
INSERT INTO tb_retirada (cd_retirada, cd_doacao, cd_instituicao, qt_retirada, ds_observacao, dt_retirada)
VALUES (sq_tb_retirada.NEXTVAL, 1, 2, 2.50, 'Para o cafe da manha', TIMESTAMP '2026-11-08 19:20:00');
INSERT INTO tb_retirada (cd_retirada, cd_doacao, cd_instituicao, qt_retirada, ds_observacao, dt_retirada)
VALUES (sq_tb_retirada.NEXTVAL, 3, 4, 15.00, 'Frutas para o lanche das criancas', TIMESTAMP '2026-11-08 10:30:00');
INSERT INTO tb_retirada (cd_retirada, cd_doacao, cd_instituicao, qt_retirada, ds_observacao, dt_retirada)
VALUES (sq_tb_retirada.NEXTVAL, 4, 4, 10.00, NULL, TIMESTAMP '2026-11-08 11:00:00');
INSERT INTO tb_retirada (cd_retirada, cd_doacao, cd_instituicao, qt_retirada, ds_observacao, dt_retirada)
VALUES (sq_tb_retirada.NEXTVAL, 5, 2, 10.00, 'Marmitas para o jantar', TIMESTAMP '2026-11-09 16:00:00');
INSERT INTO tb_retirada (cd_retirada, cd_doacao, cd_instituicao, qt_retirada, ds_observacao, dt_retirada)
VALUES (sq_tb_retirada.NEXTVAL, 8, 3, 12.00, 'Retirada total', TIMESTAMP '2026-11-08 08:30:00');
INSERT INTO tb_retirada (cd_retirada, cd_doacao, cd_instituicao, qt_retirada, ds_observacao, dt_retirada)
VALUES (sq_tb_retirada.NEXTVAL, 9, 1, 20.00, 'Para a cesta basica do mes', TIMESTAMP '2026-11-06 10:00:00');
INSERT INTO tb_retirada (cd_retirada, cd_doacao, cd_instituicao, qt_retirada, ds_observacao, dt_retirada)
VALUES (sq_tb_retirada.NEXTVAL, 10, 6, 10.00, NULL, TIMESTAMP '2026-11-06 10:30:00');
INSERT INTO tb_retirada (cd_retirada, cd_doacao, cd_instituicao, qt_retirada, ds_observacao, dt_retirada)
VALUES (sq_tb_retirada.NEXTVAL, 12, 5, 8.00, 'Retirada total', TIMESTAMP '2026-11-08 18:00:00');
INSERT INTO tb_retirada (cd_retirada, cd_doacao, cd_instituicao, qt_retirada, ds_observacao, dt_retirada)
VALUES (sq_tb_retirada.NEXTVAL, 13, 4, 6.00, 'Porcoes para o almoco comunitario', TIMESTAMP '2026-11-09 14:00:00');
INSERT INTO tb_retirada (cd_retirada, cd_doacao, cd_instituicao, qt_retirada, ds_observacao, dt_retirada)
VALUES (sq_tb_retirada.NEXTVAL, 7, 2, 5.00, 'Tomates para o molho da cozinha', TIMESTAMP '2026-11-09 09:00:00');

COMMIT;
