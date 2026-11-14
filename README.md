# Sobra Solidária (CitizenTech) — API + Banco de dados

API REST em **Spring Boot + JDBC puro (Oracle FIAP)** que conecta **estabelecimentos com sobra de alimentos**
(padarias, mercados, restaurantes) a **instituições** (bancos de alimentos, ONGs, abrigos) que podem retirá-los
antes de vencer. O mesmo banco Oracle atende a disciplina **Building Relational Database**.

Projeto das disciplinas **Domain Driven Design using Java** e **Building Relational Database** (Global Solution).

## Integrantes

| Nome | RM |
|------|----|
| _preencher_ | _preencher_ |
| _preencher_ | _preencher_ |
| _preencher_ | _preencher_ |

## Links

- **API publicada no Render:** _https://SEU-SERVICO.onrender.com_
- **Vídeo (YouTube não listado, até 5 min):** _https://youtu.be/..._

## Domínio e banco (6 tabelas)

```
Estabelecimento 1 ──── N Doacao N ──── 1 Categoria
                          1
                          │
                          N
Instituicao     1 ──── N Retirada          Instituicao N ──── N Categoria  (tb_instituicao_categoria)
```

| Tabela | Papel | Sequence |
|---|---|---|
| `tb_estabelecimento` | quem doa | `sq_tb_estabelecimento` |
| `tb_categoria` | tipo de alimento | `sq_tb_categoria` |
| `tb_instituicao` | quem retira | `sq_tb_instituicao` |
| `tb_doacao` | lote oferecido (1:N com estabelecimento e categoria) | `sq_tb_doacao` |
| `tb_instituicao_categoria` | **associativa N:N**: categorias que cada instituição aceita | (PK composta) |
| `tb_retirada` | retirada de uma doação por uma instituição (1:N com ambas) | `sq_tb_retirada` |

**Regra de negócio** (`Doacao.registrarRetirada`): só se retira de doação `DISPONIVEL`, dentro da validade e até a
quantidade restante. Ao zerar, vira `ESGOTADA` e não pode mais ser alterada. Retirada e baixa de estoque ocorrem na
**mesma transação** (`SELECT ... FOR UPDATE`).

## Scripts SQL (pasta `sql/`)

| Arquivo | O que faz |
|---|---|
| `01_criacao_tabelas.sql` | 5 sequences, 6 tabelas (PK, FK, NOT NULL, UNIQUE, CHECK), índices e `COMMENT` em cada tabela/coluna |
| `02_insercao_dados.sql` | 62 `INSERT`s |
| `03_update_delete.sql` | 2 `UPDATE` e 2 `DELETE`, todos com `WHERE` (o banco termina com **60 registros**) |
| `04_consultas.sql` | 10 consultas numeradas (Q1 a Q10) |
| `05_conferencia.sql` | contagem de registros por tabela |
| `script-completo.sql` | os cinco arquivos acima em sequência (execute no SQL Developer com **F5**) |

> Execute **uma vez**, nesta ordem. Os ids vêm das sequences (1, 2, 3...). Para refazer, use os `DROP` comentados no topo do `01`.

## Como testar (passo a passo)

### 1. Banco (Oracle SQL Developer)
1. Conecte com o usuário e a senha da FIAP.
2. Abra `sql/script-completo.sql` e execute como script (**F5**). Confira que a última consulta mostra 60 no total.
3. Tire as capturas de tela pedidas (veja `docs/documentacao-banco-de-dados.pdf`, seção 9).

### 2. API (Java)
Pré-requisitos: **JDK 17 ou superior** e **Maven 3.9+** (ou abrir no IntelliJ/Eclipse/VS Code e importar o `pom.xml`).

```bash
# Linux/macOS
export DB_USER=rmXXXXX
export DB_PASSWORD=sua_senha
mvn spring-boot:run

# Windows (PowerShell)
$env:DB_USER="rmXXXXX"; $env:DB_PASSWORD="sua_senha"
mvn spring-boot:run
```

Sobe em `http://localhost:8080`. A URL do Oracle já vem configurada (`DB_URL`, padrão
`jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL`); defina `DB_URL` se a sua for diferente.

### 3. Chamadas de teste (curl ou Postman)
```bash
curl http://localhost:8080/                                # {"api":"Sobra Solidária","status":"no ar"}
curl http://localhost:8080/estabelecimentos                # 8 estabelecimentos da carga
curl "http://localhost:8080/doacoes?status=DISPONIVEL"
curl http://localhost:8080/instituicoes/4/categorias       # lado N:N

curl -i -X POST http://localhost:8080/doacoes -H "Content-Type: application/json" -d '{
  "estabelecimentoId": 1, "categoriaId": 1, "alimento": "Bolo de cenoura",
  "descricao": "Sobra do dia", "quantidadeDisponivel": 4.5, "unidade": "kg", "dataValidade": "2026-12-20" }'

curl -i -X POST http://localhost:8080/doacoes/1/retiradas -H "Content-Type: application/json" \
  -d '{ "instituicaoId": 1, "quantidade": 2, "observacao": "Retirada as 18h" }'
```
Veja o `201` com header `Location`, e teste erros: quantidade maior que a disponível (`409`), id inexistente (`404`),
data de validade passada (`400`), `PATCH` em qualquer rota (`405`).

### 4. Testes automatizados
```bash
mvn test
```
Usam H2 em modo Oracle com o `sql/01_criacao_tabelas.sql` (não precisam do Oracle da FIAP).

### Com Docker
```bash
docker build -t sobra-solidaria .
docker run -p 8080:8080 -e DB_USER=rmXXXXX -e DB_PASSWORD=sua_senha sobra-solidaria
```

## Variáveis de ambiente

| Variável | Descrição | Padrão |
|---|---|---|
| `DB_URL` | URL JDBC do Oracle | `jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL` |
| `DB_USER` | usuário (RM) | — |
| `DB_PASSWORD` | senha | — |
| `CORS_ALLOWED_ORIGINS` | origens do front, separadas por vírgula | `*` |
| `PORT` | porta HTTP (o Render define sozinho) | `8080` |

## Endpoints

| Método | Rota | Sucesso | Erros |
|---|---|---|---|
| POST / GET | `/estabelecimentos` | 201 + Location / 200 | 400 · 409 (CNPJ repetido) |
| GET / PUT / DELETE | `/estabelecimentos/{id}` | 200 / 200 / 204 | 404 · 400 · 409 (tem doações) |
| GET | `/estabelecimentos/{id}/doacoes` | 200 | 404 |
| POST / GET | `/categorias` | 201 + Location / 200 | 400 · 409 (nome repetido) |
| GET / PUT / DELETE | `/categorias/{id}` | 200 / 200 / 204 | 404 · 400 · 409 (em uso) |
| POST / GET | `/instituicoes` | 201 + Location / 200 | 400 · 409 (CNPJ repetido) |
| GET / PUT / DELETE | `/instituicoes/{id}` | 200 / 200 / 204 | 404 · 400 · 409 (tem retiradas) |
| GET / POST | `/instituicoes/{id}/categorias` | 200 / 201 + Location | 404 · 400 · 409 (já vinculada) |
| DELETE | `/instituicoes/{id}/categorias/{categoriaId}` | 204 | 404 |
| POST / GET | `/doacoes` (`?status=DISPONIVEL`) | 201 + Location / 200 | 400 · 404 |
| GET / PUT / DELETE | `/doacoes/{id}` | 200 / 200 / 204 | 404 · 400 · 409 (esgotada / tem retiradas) |
| GET / POST | `/doacoes/{id}/retiradas` | 200 / 201 + Location | 404 · 400 · 409 (regra de negócio) |
| GET | `/doacoes/{id}/retiradas/{retiradaId}` | 200 | 404 |

Método não suportado: **405**. Erro inesperado: **500** com mensagem genérica. Todo erro sai em JSON padronizado:
```json
{ "timestamp": "2026-11-10T18:40:00", "status": 400, "erro": "Bad Request",
  "mensagem": "Dados inválidos. Verifique os campos informados.", "caminho": "/doacoes",
  "detalhes": [ { "campo": "dataValidade", "mensagem": "A data de validade deve ser futura." } ] }
```

## Estrutura do projeto

```
src/main/java/br/com/fiap/sobrasolidaria
├── model        entidades do domínio (+ regra de negócio da Doacao)
├── dao          acesso ao banco com JDBC (DataSource + PreparedStatement)
├── dto          entrada e saída da API (Bean Validation nos DTOs de entrada)
├── controller   rotas REST e status HTTP
├── exception    erros do domínio (404, 409, persistência)
├── handler      @RestControllerAdvice: JSON de erro padronizado
└── config       CORS e ModelMapper
sql/             scripts do banco (Oracle)
docs/            documentação técnica da API e do banco (PDF)
```

## Deploy no Render

1. Suba o projeto para um repositório no GitHub.
2. No Render: **New > Web Service**, escolha o repositório e o ambiente **Docker** (usa o `Dockerfile`).
3. Em **Environment**, cadastre `DB_URL`, `DB_USER`, `DB_PASSWORD` (e `CORS_ALLOWED_ORIGINS`, se quiser restringir).
4. Após o deploy, teste `GET https://SEU-SERVICO.onrender.com/`.
5. Coloque a URL acima, neste README e nos PDFs.

> No plano gratuito o Render "dorme" após alguns minutos sem uso; a primeira chamada pode demorar cerca de um minuto.
