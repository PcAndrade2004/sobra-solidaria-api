-- =====================================================================
-- Sobra Solidaria (solucao CitizenTech) - 01 - Criacao das tabelas (Oracle)
-- Ordem das chaves estrangeiras: pais primeiro, filhos depois.
-- Padrao: tb_nome, colunas cd_/nm_/ds_/dt_/qt_/nr_/st_/fl_, sequences sq_tb_nome.
-- Nomes sem espaco e sem acento.
-- =====================================================================

-- Para recriar do zero, descomente (filhos primeiro):
-- DROP TABLE tb_retirada CASCADE CONSTRAINTS;
-- DROP TABLE tb_instituicao_categoria CASCADE CONSTRAINTS;
-- DROP TABLE tb_doacao CASCADE CONSTRAINTS;
-- DROP TABLE tb_instituicao CASCADE CONSTRAINTS;
-- DROP TABLE tb_categoria CASCADE CONSTRAINTS;
-- DROP TABLE tb_estabelecimento CASCADE CONSTRAINTS;
-- DROP SEQUENCE sq_tb_retirada;
-- DROP SEQUENCE sq_tb_doacao;
-- DROP SEQUENCE sq_tb_instituicao;
-- DROP SEQUENCE sq_tb_categoria;
-- DROP SEQUENCE sq_tb_estabelecimento;

-- Sequences

CREATE SEQUENCE sq_tb_estabelecimento START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE sq_tb_categoria START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE sq_tb_instituicao START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE sq_tb_doacao START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE sq_tb_retirada START WITH 1 INCREMENT BY 1 NOCACHE;

-- tb_estabelecimento: Estabelecimentos que doam alimentos (padarias, mercados, restaurantes, feiras).
CREATE TABLE tb_estabelecimento (
    cd_estabelecimento NUMBER(10)    NOT NULL,
    nm_estabelecimento VARCHAR2(100) NOT NULL,
    nr_cnpj            VARCHAR2(14)  NOT NULL,
    ds_email           VARCHAR2(100) NOT NULL,
    ds_endereco        VARCHAR2(200) NOT NULL,
    dt_cadastro        TIMESTAMP     NOT NULL,
    CONSTRAINT pk_tb_estabelecimento PRIMARY KEY (cd_estabelecimento),
    CONSTRAINT uk_estabelecimento_cnpj UNIQUE (nr_cnpj)
);

-- tb_categoria: Categorias de alimentos (paes, hortifruti, refeicoes prontas...).
CREATE TABLE tb_categoria (
    cd_categoria NUMBER(10)    NOT NULL,
    nm_categoria VARCHAR2(60)  NOT NULL,
    ds_categoria VARCHAR2(200),
    fl_perecivel CHAR(1)       NOT NULL,
    CONSTRAINT pk_tb_categoria PRIMARY KEY (cd_categoria),
    CONSTRAINT uk_categoria_nome UNIQUE (nm_categoria),
    CONSTRAINT ck_categoria_perecivel CHECK (fl_perecivel IN ('S', 'N'))
);

-- tb_instituicao: Instituicoes que retiram alimentos para atender pessoas (bancos de alimentos, ONGs, abrigos).
CREATE TABLE tb_instituicao (
    cd_instituicao       NUMBER(10)    NOT NULL,
    nm_instituicao       VARCHAR2(100) NOT NULL,
    nr_cnpj              VARCHAR2(14)  NOT NULL,
    ds_email             VARCHAR2(100) NOT NULL,
    ds_endereco          VARCHAR2(200) NOT NULL,
    qt_pessoas_atendidas NUMBER(8)     NOT NULL,
    dt_cadastro          TIMESTAMP     NOT NULL,
    CONSTRAINT pk_tb_instituicao PRIMARY KEY (cd_instituicao),
    CONSTRAINT uk_instituicao_cnpj UNIQUE (nr_cnpj),
    CONSTRAINT ck_instituicao_pessoas CHECK (qt_pessoas_atendidas >= 0)
);

-- tb_doacao: Lotes de alimento oferecidos por um estabelecimento (1:N com estabelecimento e com categoria).
CREATE TABLE tb_doacao (
    cd_doacao          NUMBER(10)    NOT NULL,
    cd_estabelecimento NUMBER(10)    NOT NULL,
    cd_categoria       NUMBER(10)    NOT NULL,
    nm_alimento        VARCHAR2(100) NOT NULL,
    ds_alimento        VARCHAR2(300),
    qt_disponivel      NUMBER(10,2)  NOT NULL,
    ds_unidade         VARCHAR2(20)  NOT NULL,
    dt_validade        DATE          NOT NULL,
    st_doacao          VARCHAR2(15)  NOT NULL,
    dt_cadastro        TIMESTAMP     NOT NULL,
    CONSTRAINT pk_tb_doacao PRIMARY KEY (cd_doacao),
    CONSTRAINT fk_doacao_estabelecimento FOREIGN KEY (cd_estabelecimento) REFERENCES tb_estabelecimento (cd_estabelecimento),
    CONSTRAINT fk_doacao_categoria FOREIGN KEY (cd_categoria) REFERENCES tb_categoria (cd_categoria),
    CONSTRAINT ck_doacao_quantidade CHECK (qt_disponivel >= 0),
    CONSTRAINT ck_doacao_status CHECK (st_doacao IN ('DISPONIVEL', 'ESGOTADA'))
);
CREATE INDEX ix_doacao_estabelecimento ON tb_doacao (cd_estabelecimento);
CREATE INDEX ix_doacao_categoria ON tb_doacao (cd_categoria);

-- tb_instituicao_categoria: Tabela associativa N:N: categorias de alimento que cada instituicao aceita receber.
CREATE TABLE tb_instituicao_categoria (
    cd_instituicao NUMBER(10)    NOT NULL,
    cd_categoria   NUMBER(10)    NOT NULL,
    dt_vinculo     TIMESTAMP     NOT NULL,
    CONSTRAINT pk_tb_instituicao_categoria PRIMARY KEY (cd_instituicao, cd_categoria),
    CONSTRAINT fk_instcat_instituicao FOREIGN KEY (cd_instituicao) REFERENCES tb_instituicao (cd_instituicao),
    CONSTRAINT fk_instcat_categoria FOREIGN KEY (cd_categoria) REFERENCES tb_categoria (cd_categoria)
);
CREATE INDEX ix_instcat_categoria ON tb_instituicao_categoria (cd_categoria);

-- tb_retirada: Retiradas de (parte de) uma doacao feitas por uma instituicao (1:N com doacao e com instituicao).
CREATE TABLE tb_retirada (
    cd_retirada    NUMBER(10)    NOT NULL,
    cd_doacao      NUMBER(10)    NOT NULL,
    cd_instituicao NUMBER(10)    NOT NULL,
    qt_retirada    NUMBER(10,2)  NOT NULL,
    ds_observacao  VARCHAR2(300),
    dt_retirada    TIMESTAMP     NOT NULL,
    CONSTRAINT pk_tb_retirada PRIMARY KEY (cd_retirada),
    CONSTRAINT fk_retirada_doacao FOREIGN KEY (cd_doacao) REFERENCES tb_doacao (cd_doacao),
    CONSTRAINT fk_retirada_instituicao FOREIGN KEY (cd_instituicao) REFERENCES tb_instituicao (cd_instituicao),
    CONSTRAINT ck_retirada_quantidade CHECK (qt_retirada > 0)
);
CREATE INDEX ix_retirada_doacao ON tb_retirada (cd_doacao);
CREATE INDEX ix_retirada_instituicao ON tb_retirada (cd_instituicao);

-- Comentarios das tabelas e colunas (dicionario no proprio banco)
COMMENT ON TABLE tb_estabelecimento IS 'Estabelecimentos que doam alimentos (padarias, mercados, restaurantes, feiras).';
COMMENT ON COLUMN tb_estabelecimento.cd_estabelecimento IS 'Identificador do estabelecimento (sequence sq_tb_estabelecimento).';
COMMENT ON COLUMN tb_estabelecimento.nm_estabelecimento IS 'Nome fantasia do estabelecimento.';
COMMENT ON COLUMN tb_estabelecimento.nr_cnpj IS 'CNPJ com 14 digitos, sem pontuacao.';
COMMENT ON COLUMN tb_estabelecimento.ds_email IS 'E-mail de contato.';
COMMENT ON COLUMN tb_estabelecimento.ds_endereco IS 'Endereco onde o alimento e retirado.';
COMMENT ON COLUMN tb_estabelecimento.dt_cadastro IS 'Data e hora do cadastro.';
COMMENT ON TABLE tb_categoria IS 'Categorias de alimentos (paes, hortifruti, refeicoes prontas...).';
COMMENT ON COLUMN tb_categoria.cd_categoria IS 'Identificador da categoria (sequence sq_tb_categoria).';
COMMENT ON COLUMN tb_categoria.nm_categoria IS 'Nome da categoria.';
COMMENT ON COLUMN tb_categoria.ds_categoria IS 'Descricao da categoria.';
COMMENT ON COLUMN tb_categoria.fl_perecivel IS 'S se o alimento da categoria e perecivel, N se nao e.';
COMMENT ON TABLE tb_instituicao IS 'Instituicoes que retiram alimentos para atender pessoas (bancos de alimentos, ONGs, abrigos).';
COMMENT ON COLUMN tb_instituicao.cd_instituicao IS 'Identificador da instituicao (sequence sq_tb_instituicao).';
COMMENT ON COLUMN tb_instituicao.nm_instituicao IS 'Nome da instituicao.';
COMMENT ON COLUMN tb_instituicao.nr_cnpj IS 'CNPJ com 14 digitos, sem pontuacao.';
COMMENT ON COLUMN tb_instituicao.ds_email IS 'E-mail de contato.';
COMMENT ON COLUMN tb_instituicao.ds_endereco IS 'Endereco da instituicao.';
COMMENT ON COLUMN tb_instituicao.qt_pessoas_atendidas IS 'Quantidade de pessoas atendidas por mes.';
COMMENT ON COLUMN tb_instituicao.dt_cadastro IS 'Data e hora do cadastro.';
COMMENT ON TABLE tb_doacao IS 'Lotes de alimento oferecidos por um estabelecimento (1:N com estabelecimento e com categoria).';
COMMENT ON COLUMN tb_doacao.cd_doacao IS 'Identificador da doacao (sequence sq_tb_doacao).';
COMMENT ON COLUMN tb_doacao.cd_estabelecimento IS 'Estabelecimento que doa (tb_estabelecimento).';
COMMENT ON COLUMN tb_doacao.cd_categoria IS 'Categoria do alimento (tb_categoria).';
COMMENT ON COLUMN tb_doacao.nm_alimento IS 'Nome do alimento doado.';
COMMENT ON COLUMN tb_doacao.ds_alimento IS 'Detalhes do alimento.';
COMMENT ON COLUMN tb_doacao.qt_disponivel IS 'Quantidade que ainda esta disponivel para retirada.';
COMMENT ON COLUMN tb_doacao.ds_unidade IS 'Unidade de medida (kg, un, L).';
COMMENT ON COLUMN tb_doacao.dt_validade IS 'Data limite de consumo.';
COMMENT ON COLUMN tb_doacao.st_doacao IS 'Situacao da doacao.';
COMMENT ON COLUMN tb_doacao.dt_cadastro IS 'Data e hora do cadastro.';
COMMENT ON TABLE tb_instituicao_categoria IS 'Tabela associativa N:N: categorias de alimento que cada instituicao aceita receber.';
COMMENT ON COLUMN tb_instituicao_categoria.cd_instituicao IS 'Instituicao (tb_instituicao).';
COMMENT ON COLUMN tb_instituicao_categoria.cd_categoria IS 'Categoria aceita (tb_categoria).';
COMMENT ON COLUMN tb_instituicao_categoria.dt_vinculo IS 'Data e hora em que a instituicao passou a aceitar a categoria.';
COMMENT ON TABLE tb_retirada IS 'Retiradas de (parte de) uma doacao feitas por uma instituicao (1:N com doacao e com instituicao).';
COMMENT ON COLUMN tb_retirada.cd_retirada IS 'Identificador da retirada (sequence sq_tb_retirada).';
COMMENT ON COLUMN tb_retirada.cd_doacao IS 'Doacao da qual se retirou (tb_doacao).';
COMMENT ON COLUMN tb_retirada.cd_instituicao IS 'Instituicao que retirou (tb_instituicao).';
COMMENT ON COLUMN tb_retirada.qt_retirada IS 'Quantidade retirada.';
COMMENT ON COLUMN tb_retirada.ds_observacao IS 'Observacao livre sobre a retirada.';
COMMENT ON COLUMN tb_retirada.dt_retirada IS 'Data e hora da retirada.';
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
-- Conferencia da contagem de registros (esperado apos os scripts 01 a 03: 60 no total)
SELECT 'tb_estabelecimento' AS tabela, COUNT(*) AS total FROM tb_estabelecimento
UNION ALL SELECT 'tb_categoria', COUNT(*) FROM tb_categoria
UNION ALL SELECT 'tb_instituicao', COUNT(*) FROM tb_instituicao
UNION ALL SELECT 'tb_instituicao_categoria', COUNT(*) FROM tb_instituicao_categoria
UNION ALL SELECT 'tb_doacao', COUNT(*) FROM tb_doacao
UNION ALL SELECT 'tb_retirada', COUNT(*) FROM tb_retirada;
