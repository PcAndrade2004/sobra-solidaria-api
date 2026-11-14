package br.com.fiap.sobrasolidaria;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIntegrationTest {

    private static final AtomicLong CONTADOR = new AtomicLong(10_000_000_000_000L);

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper json;

    // ------------------------------------------------------------ helpers

    private long proximo() {
        return CONTADOR.incrementAndGet();
    }

    private String estabelecimentoJson(String nome, String cnpj) {
        return """
                {"nome":"%s","cnpj":"%s","email":"contato@padaria.com.br","endereco":"Rua das Flores, 100 - São Paulo"}
                """.formatted(nome, cnpj);
    }

    private String instituicaoJson(String nome, String cnpj, int pessoas) {
        return """
                {"nome":"%s","cnpj":"%s","email":"ong@banco.org.br","endereco":"Av. Solidariedade, 800 - Cotia",
                 "pessoasAtendidas":%d}
                """.formatted(nome, cnpj, pessoas);
    }

    private String categoriaJson(String nome) {
        return "{\"nome\":\"%s\",\"descricao\":\"Categoria de teste\",\"perecivel\":true}".formatted(nome);
    }

    private String doacaoJson(long estabelecimentoId, long categoriaId, String quantidade, LocalDate validade) {
        return """
                {"estabelecimentoId":%d,"categoriaId":%d,"alimento":"Pão francês","descricao":"Sobra do dia",
                 "quantidadeDisponivel":%s,"unidade":"kg","dataValidade":"%s"}
                """.formatted(estabelecimentoId, categoriaId, quantidade, validade);
    }

    private String retiradaJson(long instituicaoId, String quantidade) {
        return "{\"instituicaoId\":%d,\"quantidade\":%s,\"observacao\":\"Retirada às 18h\"}"
                .formatted(instituicaoId, quantidade);
    }

    private long idDe(String corpo) throws Exception {
        return json.readTree(corpo).get("id").asLong();
    }

    private long criarEstabelecimento() throws Exception {
        return idDe(mvc.perform(post("/estabelecimentos").contentType(MediaType.APPLICATION_JSON)
                        .content(estabelecimentoJson("Padaria Central", String.valueOf(proximo()))))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
    }

    private long criarInstituicao() throws Exception {
        return idDe(mvc.perform(post("/instituicoes").contentType(MediaType.APPLICATION_JSON)
                        .content(instituicaoJson("Banco de Alimentos", String.valueOf(proximo()), 300)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
    }

    private long criarCategoria() throws Exception {
        return idDe(mvc.perform(post("/categorias").contentType(MediaType.APPLICATION_JSON)
                        .content(categoriaJson("Categoria " + proximo())))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
    }

    private long criarDoacao(long estabelecimentoId, String quantidade) throws Exception {
        return idDe(mvc.perform(post("/doacoes").contentType(MediaType.APPLICATION_JSON)
                        .content(doacaoJson(estabelecimentoId, criarCategoria(), quantidade,
                                LocalDate.now().plusDays(3))))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
    }

    /** Lê a quantidade disponível como BigDecimal (o JSON pode vir como 6, 6.0 ou 6.00). */
    private void assertQuantidade(String esperado, long doacao) throws Exception {
        String corpo = mvc.perform(get("/doacoes/{id}", doacao)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        BigDecimal atual = json.readTree(corpo).get("quantidadeDisponivel").decimalValue();
        assertEquals(0, new BigDecimal(esperado).compareTo(atual), "quantidade disponível da doação " + doacao);
    }

    // ------------------------------------------------------------ estabelecimentos

    @Test
    void cadastraEstabelecimentoComLocationEBuscaPorId() throws Exception {
        mvc.perform(post("/estabelecimentos").contentType(MediaType.APPLICATION_JSON)
                        .content(estabelecimentoJson("Mercado Bom Preço", String.valueOf(proximo()))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/estabelecimentos/")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Mercado Bom Preço"))
                .andExpect(jsonPath("$.dataCadastro").exists());

        long id = criarEstabelecimento();
        mvc.perform(get("/estabelecimentos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
        mvc.perform(get("/estabelecimentos")).andExpect(status().isOk());
    }

    @Test
    void cnpjDuplicadoRetorna409() throws Exception {
        String cnpj = String.valueOf(proximo());
        mvc.perform(post("/estabelecimentos").contentType(MediaType.APPLICATION_JSON)
                .content(estabelecimentoJson("A", cnpj))).andExpect(status().isCreated());
        mvc.perform(post("/estabelecimentos").contentType(MediaType.APPLICATION_JSON)
                        .content(estabelecimentoJson("B", cnpj)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensagem").value(containsString("CNPJ")));
    }

    @Test
    void validacaoRetorna400ComDetalhesPorCampo() throws Exception {
        mvc.perform(post("/estabelecimentos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\",\"cnpj\":\"123\",\"email\":\"invalido\",\"endereco\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detalhes", hasSize(4)))
                .andExpect(jsonPath("$.detalhes[0].campo").exists());
    }

    @Test
    void buscaInexistenteRetorna404() throws Exception {
        mvc.perform(get("/estabelecimentos/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.caminho").value("/estabelecimentos/999999"));
    }

    @Test
    void atualizaEstabelecimentoEValidaErros() throws Exception {
        String cnpjDele = String.valueOf(proximo());
        long id = idDe(mvc.perform(post("/estabelecimentos").contentType(MediaType.APPLICATION_JSON)
                        .content(estabelecimentoJson("Padaria", cnpjDele)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
        String outroCnpj = String.valueOf(proximo());
        mvc.perform(post("/estabelecimentos").contentType(MediaType.APPLICATION_JSON)
                .content(estabelecimentoJson("Outro", outroCnpj))).andExpect(status().isCreated());

        // 200: mantém o próprio CNPJ e muda o nome
        mvc.perform(put("/estabelecimentos/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(estabelecimentoJson("Padaria Renovada", cnpjDele)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Padaria Renovada"));
        // 409: CNPJ de outro estabelecimento
        mvc.perform(put("/estabelecimentos/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(estabelecimentoJson("X", outroCnpj)))
                .andExpect(status().isConflict());
        // 404
        mvc.perform(put("/estabelecimentos/{id}", 999999).contentType(MediaType.APPLICATION_JSON)
                        .content(estabelecimentoJson("X", String.valueOf(proximo()))))
                .andExpect(status().isNotFound());
        // 400
        mvc.perform(put("/estabelecimentos/{id}", id).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------ categorias

    @Test
    void crudDeCategoriaComRegras() throws Exception {
        String nome = "Categoria unica " + proximo();
        long id = idDe(mvc.perform(post("/categorias").contentType(MediaType.APPLICATION_JSON)
                        .content(categoriaJson(nome)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/categorias/")))
                .andExpect(jsonPath("$.perecivel").value(true))
                .andReturn().getResponse().getContentAsString());

        mvc.perform(post("/categorias").contentType(MediaType.APPLICATION_JSON).content(categoriaJson(nome)))
                .andExpect(status().isConflict());
        mvc.perform(post("/categorias").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\",\"descricao\":\"x\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/categorias/{id}", id)).andExpect(status().isOk());
        mvc.perform(put("/categorias/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"" + nome + " v2\",\"perecivel\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.perecivel").value(false));
        mvc.perform(get("/categorias/{id}", 999999)).andExpect(status().isNotFound());
        mvc.perform(delete("/categorias/{id}", id)).andExpect(status().isNoContent());
        mvc.perform(delete("/categorias/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void categoriaEmUsoNaoPodeSerRemovida() throws Exception {
        long categoria = criarCategoria();
        mvc.perform(post("/doacoes").contentType(MediaType.APPLICATION_JSON)
                        .content(doacaoJson(criarEstabelecimento(), categoria, "5", LocalDate.now().plusDays(2))))
                .andExpect(status().isCreated());
        mvc.perform(delete("/categorias/{id}", categoria))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value(containsString("vinculadas")));
    }

    // ------------------------------------------------------------ instituições e N:N

    @Test
    void crudDeInstituicaoComRegras() throws Exception {
        String cnpj = String.valueOf(proximo());
        long id = idDe(mvc.perform(post("/instituicoes").contentType(MediaType.APPLICATION_JSON)
                        .content(instituicaoJson("ONG Prato Cheio", cnpj, 120)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/instituicoes/")))
                .andExpect(jsonPath("$.pessoasAtendidas").value(120))
                .andReturn().getResponse().getContentAsString());

        mvc.perform(post("/instituicoes").contentType(MediaType.APPLICATION_JSON)
                        .content(instituicaoJson("Outra", cnpj, 10)))
                .andExpect(status().isConflict());
        mvc.perform(post("/instituicoes").contentType(MediaType.APPLICATION_JSON)
                        .content(instituicaoJson("Negativa", String.valueOf(proximo()), -5)))
                .andExpect(status().isBadRequest());
        mvc.perform(put("/instituicoes/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(instituicaoJson("ONG Prato Cheio II", cnpj, 200)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pessoasAtendidas").value(200));
        mvc.perform(get("/instituicoes/{id}", 999999)).andExpect(status().isNotFound());
        mvc.perform(delete("/instituicoes/{id}", id)).andExpect(status().isNoContent());
    }

    @Test
    void relacionamentoNparaNVinculaEDesvinculaCategorias() throws Exception {
        long instituicao = criarInstituicao();
        long categoria = criarCategoria();

        mvc.perform(get("/instituicoes/{id}/categorias", instituicao))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));

        mvc.perform(post("/instituicoes/{id}/categorias", instituicao).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoriaId\":" + categoria + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(categoria));
        mvc.perform(get("/instituicoes/{id}/categorias", instituicao)).andExpect(jsonPath("$", hasSize(1)));

        // 409: vínculo repetido; 404: categoria ou instituição inexistente; 400: corpo inválido
        mvc.perform(post("/instituicoes/{id}/categorias", instituicao).contentType(MediaType.APPLICATION_JSON)
                .content("{\"categoriaId\":" + categoria + "}")).andExpect(status().isConflict());
        mvc.perform(post("/instituicoes/{id}/categorias", instituicao).contentType(MediaType.APPLICATION_JSON)
                .content("{\"categoriaId\":999999}")).andExpect(status().isNotFound());
        mvc.perform(post("/instituicoes/{id}/categorias", 999999).contentType(MediaType.APPLICATION_JSON)
                .content("{\"categoriaId\":" + categoria + "}")).andExpect(status().isNotFound());
        mvc.perform(post("/instituicoes/{id}/categorias", instituicao).contentType(MediaType.APPLICATION_JSON)
                .content("{}")).andExpect(status().isBadRequest());

        // categoria vinculada não pode ser removida
        mvc.perform(delete("/categorias/{id}", categoria)).andExpect(status().isConflict());

        mvc.perform(delete("/instituicoes/{id}/categorias/{c}", instituicao, categoria))
                .andExpect(status().isNoContent());
        mvc.perform(delete("/instituicoes/{id}/categorias/{c}", instituicao, categoria))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/categorias/{id}", categoria)).andExpect(status().isNoContent());
    }

    @Test
    void removerInstituicaoApagaSeusVinculos() throws Exception {
        long instituicao = criarInstituicao();
        long categoria = criarCategoria();
        mvc.perform(post("/instituicoes/{id}/categorias", instituicao).contentType(MediaType.APPLICATION_JSON)
                .content("{\"categoriaId\":" + categoria + "}")).andExpect(status().isCreated());

        mvc.perform(delete("/instituicoes/{id}", instituicao)).andExpect(status().isNoContent());
        mvc.perform(delete("/categorias/{id}", categoria)).andExpect(status().isNoContent());
    }

    // ------------------------------------------------------------ doações

    @Test
    void cadastraDoacaoComRegrasDeValidacao() throws Exception {
        long estabelecimento = criarEstabelecimento();
        long categoria = criarCategoria();

        mvc.perform(post("/doacoes").contentType(MediaType.APPLICATION_JSON)
                        .content(doacaoJson(estabelecimento, categoria, "10.5", LocalDate.now().plusDays(2))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/doacoes/")))
                .andExpect(jsonPath("$.status").value("DISPONIVEL"))
                .andExpect(jsonPath("$.estabelecimentoId").value(estabelecimento))
                .andExpect(jsonPath("$.categoriaId").value(categoria))
                .andExpect(jsonPath("$.quantidadeDisponivel").isNumber());

        // 400: validade no passado
        mvc.perform(post("/doacoes").contentType(MediaType.APPLICATION_JSON)
                        .content(doacaoJson(estabelecimento, categoria, "10", LocalDate.now().minusDays(1))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalhes[0].campo").value("dataValidade"));
        // 400: quantidade negativa
        mvc.perform(post("/doacoes").contentType(MediaType.APPLICATION_JSON)
                        .content(doacaoJson(estabelecimento, categoria, "-1", LocalDate.now().plusDays(2))))
                .andExpect(status().isBadRequest());
        // 404: estabelecimento ou categoria inexistente
        mvc.perform(post("/doacoes").contentType(MediaType.APPLICATION_JSON)
                        .content(doacaoJson(999999, categoria, "10", LocalDate.now().plusDays(2))))
                .andExpect(status().isNotFound());
        mvc.perform(post("/doacoes").contentType(MediaType.APPLICATION_JSON)
                        .content(doacaoJson(estabelecimento, 999999, "10", LocalDate.now().plusDays(2))))
                .andExpect(status().isNotFound());
    }

    @Test
    void listaDoacoesDoEstabelecimentoEFiltraPorStatus() throws Exception {
        long estabelecimento = criarEstabelecimento();
        criarDoacao(estabelecimento, "5");
        criarDoacao(estabelecimento, "7");

        mvc.perform(get("/estabelecimentos/{id}/doacoes", estabelecimento))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        mvc.perform(get("/estabelecimentos/{id}/doacoes", 999999)).andExpect(status().isNotFound());
        mvc.perform(get("/doacoes").param("status", "DISPONIVEL")).andExpect(status().isOk());
        mvc.perform(get("/doacoes").param("status", "QUALQUER")).andExpect(status().isBadRequest());
    }

    @Test
    void atualizaERemoveDoacao() throws Exception {
        long estabelecimento = criarEstabelecimento();
        long categoria = criarCategoria();
        long doacao = idDe(mvc.perform(post("/doacoes").contentType(MediaType.APPLICATION_JSON)
                        .content(doacaoJson(estabelecimento, categoria, "5", LocalDate.now().plusDays(3))))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());

        mvc.perform(put("/doacoes/{id}", doacao).contentType(MediaType.APPLICATION_JSON)
                        .content(doacaoJson(estabelecimento, categoria, "8", LocalDate.now().plusDays(5))))
                .andExpect(status().isOk());
        assertQuantidade("8", doacao);
        mvc.perform(put("/doacoes/{id}", 999999).contentType(MediaType.APPLICATION_JSON)
                        .content(doacaoJson(estabelecimento, categoria, "8", LocalDate.now().plusDays(5))))
                .andExpect(status().isNotFound());

        mvc.perform(delete("/doacoes/{id}", doacao)).andExpect(status().isNoContent());
        mvc.perform(get("/doacoes/{id}", doacao)).andExpect(status().isNotFound());
        mvc.perform(delete("/doacoes/{id}", doacao)).andExpect(status().isNotFound());
    }

    // ------------------------------------------------------------ retiradas (regra de negócio)

    @Test
    void retiradaBaixaEstoqueEEsgotaDoacao() throws Exception {
        long estabelecimento = criarEstabelecimento();
        long instituicao = criarInstituicao();
        long categoria = criarCategoria();
        long doacao = idDe(mvc.perform(post("/doacoes").contentType(MediaType.APPLICATION_JSON)
                        .content(doacaoJson(estabelecimento, categoria, "10", LocalDate.now().plusDays(3))))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());

        mvc.perform(post("/doacoes/{id}/retiradas", doacao).contentType(MediaType.APPLICATION_JSON)
                        .content(retiradaJson(instituicao, "4")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/doacoes/" + doacao + "/retiradas/")))
                .andExpect(jsonPath("$.doacaoId").value(doacao))
                .andExpect(jsonPath("$.instituicaoId").value(instituicao));

        assertQuantidade("6", doacao);
        mvc.perform(get("/doacoes/{id}", doacao)).andExpect(jsonPath("$.status").value("DISPONIVEL"));

        // 409: mais do que o disponível; nada é gravado
        mvc.perform(post("/doacoes/{id}/retiradas", doacao).contentType(MediaType.APPLICATION_JSON)
                        .content(retiradaJson(instituicao, "7")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value(containsString("maior que a disponível")));
        mvc.perform(get("/doacoes/{id}/retiradas", doacao)).andExpect(jsonPath("$", hasSize(1)));

        // retira o restante: doação esgotada
        mvc.perform(post("/doacoes/{id}/retiradas", doacao).contentType(MediaType.APPLICATION_JSON)
                .content(retiradaJson(instituicao, "6"))).andExpect(status().isCreated());
        assertQuantidade("0", doacao);
        mvc.perform(get("/doacoes/{id}", doacao)).andExpect(jsonPath("$.status").value("ESGOTADA"));

        // 409: doação esgotada não aceita retirada nem alteração
        mvc.perform(post("/doacoes/{id}/retiradas", doacao).contentType(MediaType.APPLICATION_JSON)
                .content(retiradaJson(instituicao, "1"))).andExpect(status().isConflict());
        mvc.perform(put("/doacoes/{id}", doacao).contentType(MediaType.APPLICATION_JSON)
                        .content(doacaoJson(estabelecimento, categoria, "3", LocalDate.now().plusDays(5))))
                .andExpect(status().isConflict());
    }

    @Test
    void retiradaValidaEntradaEExistenciaDosRecursos() throws Exception {
        long instituicao = criarInstituicao();
        long doacao = criarDoacao(criarEstabelecimento(), "10");

        mvc.perform(post("/doacoes/{id}/retiradas", doacao).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantidade\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalhes", hasSize(2)));
        mvc.perform(post("/doacoes/{id}/retiradas", 999999).contentType(MediaType.APPLICATION_JSON)
                        .content(retiradaJson(instituicao, "1")))
                .andExpect(status().isNotFound());
        mvc.perform(post("/doacoes/{id}/retiradas", doacao).contentType(MediaType.APPLICATION_JSON)
                        .content(retiradaJson(999999, "1")))
                .andExpect(status().isNotFound());
        mvc.perform(get("/doacoes/{id}/retiradas", 999999)).andExpect(status().isNotFound());
        mvc.perform(get("/doacoes/{id}/retiradas/{r}", doacao, 999999)).andExpect(status().isNotFound());
    }

    @Test
    void removerRegistroComFilhosRetorna409() throws Exception {
        long estabelecimento = criarEstabelecimento();
        long instituicao = criarInstituicao();
        long doacao = criarDoacao(estabelecimento, "10");
        mvc.perform(post("/doacoes/{id}/retiradas", doacao).contentType(MediaType.APPLICATION_JSON)
                .content(retiradaJson(instituicao, "1"))).andExpect(status().isCreated());

        mvc.perform(delete("/doacoes/{id}", doacao))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value(containsString("retiradas")));
        mvc.perform(delete("/estabelecimentos/{id}", estabelecimento))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value(containsString("doações")));
        mvc.perform(delete("/instituicoes/{id}", instituicao))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value(containsString("retiradas")));
    }

    @Test
    void removeEstabelecimentoSemFilhosRetorna204() throws Exception {
        long id = criarEstabelecimento();
        mvc.perform(delete("/estabelecimentos/{id}", id)).andExpect(status().isNoContent());
        mvc.perform(delete("/estabelecimentos/{id}", id)).andExpect(status().isNotFound());
    }

    // ------------------------------------------------------------ erros gerais e CORS

    @Test
    void metodoNaoSuportadoRetorna405() throws Exception {
        mvc.perform(patch("/estabelecimentos/{id}", 1))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405));
    }

    @Test
    void jsonMalFormadoRetorna400SemVazarDetalhes() throws Exception {
        mvc.perform(post("/estabelecimentos").contentType(MediaType.APPLICATION_JSON).content("{nome:"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value(not(containsString("Jackson"))))
                .andExpect(jsonPath("$.trace").doesNotExist());
    }

    @Test
    void rotaInexistenteRetorna404Padronizado() throws Exception {
        mvc.perform(get("/nao-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Rota não encontrada."));
    }

    @Test
    void corsLiberadoParaOFront() throws Exception {
        mvc.perform(options("/estabelecimentos")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().string("Access-Control-Allow-Methods", containsString("POST")));
        mvc.perform(get("/estabelecimentos").header("Origin", "http://localhost:5173"))
                .andExpect(header().exists("Access-Control-Allow-Origin"));
    }

    @Test
    void raizRespondeParaChecagemDeSaude() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("no ar"));
    }
}
