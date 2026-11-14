package br.com.fiap.sobrasolidaria.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.fiap.sobrasolidaria.dao.CategoriaDao;
import br.com.fiap.sobrasolidaria.dao.DoacaoDao;
import br.com.fiap.sobrasolidaria.dao.EstabelecimentoDao;
import br.com.fiap.sobrasolidaria.dao.RetiradaDao;
import br.com.fiap.sobrasolidaria.dto.DoacaoRequestDTO;
import br.com.fiap.sobrasolidaria.dto.DoacaoResponseDTO;
import br.com.fiap.sobrasolidaria.exception.RecursoNaoEncontradoException;
import br.com.fiap.sobrasolidaria.exception.RegraNegocioException;
import br.com.fiap.sobrasolidaria.model.Doacao;
import br.com.fiap.sobrasolidaria.model.StatusDoacao;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/doacoes")
public class DoacaoController {

    private final DoacaoDao doacaoDao;
    private final EstabelecimentoDao estabelecimentoDao;
    private final RetiradaDao retiradaDao;
    private final CategoriaDao categoriaDao;
    private final ModelMapper mapper;

    public DoacaoController(DoacaoDao doacaoDao, EstabelecimentoDao estabelecimentoDao,
                            RetiradaDao retiradaDao, CategoriaDao categoriaDao, ModelMapper mapper) {
        this.doacaoDao = doacaoDao;
        this.estabelecimentoDao = estabelecimentoDao;
        this.retiradaDao = retiradaDao;
        this.categoriaDao = categoriaDao;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<DoacaoResponseDTO> cadastrar(@Valid @RequestBody DoacaoRequestDTO dto) {
        garantirEstabelecimento(dto.getEstabelecimentoId());
        garantirCategoria(dto.getCategoriaId());

        Doacao doacao = mapper.map(dto, Doacao.class);
        doacao.setStatus(StatusDoacao.DISPONIVEL);
        doacao.setDataCadastro(LocalDateTime.now());
        doacaoDao.inserir(doacao);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(doacao.getId()).toUri();
        return ResponseEntity.created(location).body(paraResposta(doacao));
    }

    /** Lista as doações. Filtro opcional: /doacoes?status=DISPONIVEL ou ESGOTADA. */
    @GetMapping
    public List<DoacaoResponseDTO> listar(@RequestParam(required = false) StatusDoacao status) {
        return doacaoDao.listar(status).stream().map(this::paraResposta).toList();
    }

    @GetMapping("/{id}")
    public DoacaoResponseDTO buscarPorId(@PathVariable Long id) {
        return paraResposta(buscarOuFalhar(id));
    }

    @PutMapping("/{id}")
    public DoacaoResponseDTO atualizar(@PathVariable Long id, @Valid @RequestBody DoacaoRequestDTO dto) {
        Doacao existente = buscarOuFalhar(id);
        if (existente.getStatus() == StatusDoacao.ESGOTADA) {
            throw new RegraNegocioException("Uma doação esgotada não pode ser alterada.");
        }
        garantirEstabelecimento(dto.getEstabelecimentoId());
        garantirCategoria(dto.getCategoriaId());

        existente.setEstabelecimentoId(dto.getEstabelecimentoId());
        existente.setCategoriaId(dto.getCategoriaId());
        existente.setAlimento(dto.getAlimento());
        existente.setDescricao(dto.getDescricao());
        existente.setQuantidadeDisponivel(dto.getQuantidadeDisponivel());
        existente.setUnidade(dto.getUnidade());
        existente.setDataValidade(dto.getDataValidade());
        doacaoDao.atualizar(existente);
        return paraResposta(existente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        buscarOuFalhar(id);
        if (retiradaDao.possuiRetiradas(id)) {
            throw new RegraNegocioException("Não é possível remover a doação: ela possui retiradas registradas.");
        }
        doacaoDao.remover(id);
        return ResponseEntity.noContent().build();
    }

    private Doacao buscarOuFalhar(Long id) {
        return doacaoDao.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Doação " + id + " não encontrada."));
    }

    private void garantirEstabelecimento(Long estabelecimentoId) {
        if (estabelecimentoDao.buscarPorId(estabelecimentoId).isEmpty()) {
            throw new RecursoNaoEncontradoException("Estabelecimento " + estabelecimentoId + " não encontrado.");
        }
    }

    private void garantirCategoria(Long categoriaId) {
        if (categoriaDao.buscarPorId(categoriaId).isEmpty()) {
            throw new RecursoNaoEncontradoException("Categoria " + categoriaId + " não encontrada.");
        }
    }

    private DoacaoResponseDTO paraResposta(Doacao doacao) {
        return mapper.map(doacao, DoacaoResponseDTO.class);
    }
}
