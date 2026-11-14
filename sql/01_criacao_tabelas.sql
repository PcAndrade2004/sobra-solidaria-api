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
