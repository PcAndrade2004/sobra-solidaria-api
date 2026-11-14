package br.com.fiap.sobrasolidaria.controller;

import java.net.URI;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.fiap.sobrasolidaria.dao.CategoriaDao;
import br.com.fiap.sobrasolidaria.dao.DoacaoDao;
import br.com.fiap.sobrasolidaria.dao.InstituicaoDao;
import br.com.fiap.sobrasolidaria.dto.CategoriaRequestDTO;
import br.com.fiap.sobrasolidaria.dto.CategoriaResponseDTO;
import br.com.fiap.sobrasolidaria.exception.RecursoNaoEncontradoException;
import br.com.fiap.sobrasolidaria.exception.RegraNegocioException;
import br.com.fiap.sobrasolidaria.model.Categoria;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaDao categoriaDao;
    private final DoacaoDao doacaoDao;
    private final InstituicaoDao instituicaoDao;
    private final ModelMapper mapper;

    public CategoriaController(CategoriaDao categoriaDao, DoacaoDao doacaoDao, InstituicaoDao instituicaoDao,
                               ModelMapper mapper) {
        this.categoriaDao = categoriaDao;
        this.doacaoDao = doacaoDao;
        this.instituicaoDao = instituicaoDao;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> cadastrar(@Valid @RequestBody CategoriaRequestDTO dto) {
        validarNomeUnico(dto.getNome(), null);
        Categoria categoria = mapper.map(dto, Categoria.class);
        categoriaDao.inserir(categoria);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(categoria.getId()).toUri();
        return ResponseEntity.created(location).body(paraResposta(categoria));
    }

    @GetMapping
    public List<CategoriaResponseDTO> listar() {
        return categoriaDao.listar().stream().map(this::paraResposta).toList();
    }

    @GetMapping("/{id}")
    public CategoriaResponseDTO buscarPorId(@PathVariable Long id) {
        return paraResposta(buscarOuFalhar(id));
    }

    @PutMapping("/{id}")
    public CategoriaResponseDTO atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequestDTO dto) {
        Categoria existente = buscarOuFalhar(id);
        validarNomeUnico(dto.getNome(), id);

        existente.setNome(dto.getNome());
        existente.setDescricao(dto.getDescricao());
        existente.setPerecivel(dto.getPerecivel());
        categoriaDao.atualizar(existente);
        return paraResposta(existente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        buscarOuFalhar(id);
        if (doacaoDao.possuiDoacoesDaCategoria(id) || instituicaoDao.possuiVinculosDaCategoria(id)) {
            throw new RegraNegocioException(
                    "Não é possível remover a categoria: ela possui doações ou instituições vinculadas.");
        }
        categoriaDao.remover(id);
        return ResponseEntity.noContent().build();
    }

    private Categoria buscarOuFalhar(Long id) {
        return categoriaDao.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria " + id + " não encontrada."));
    }

    private void validarNomeUnico(String nome, Long ignorarId) {
        if (categoriaDao.existePorNome(nome, ignorarId)) {
            throw new RegraNegocioException("Já existe uma categoria com este nome.");
        }
    }

    private CategoriaResponseDTO paraResposta(Categoria categoria) {
        return mapper.map(categoria, CategoriaResponseDTO.class);
    }
}
