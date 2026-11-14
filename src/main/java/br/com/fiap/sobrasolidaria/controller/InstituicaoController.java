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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.fiap.sobrasolidaria.dao.CategoriaDao;
import br.com.fiap.sobrasolidaria.dao.InstituicaoDao;
import br.com.fiap.sobrasolidaria.dao.RetiradaDao;
import br.com.fiap.sobrasolidaria.dto.CategoriaResponseDTO;
import br.com.fiap.sobrasolidaria.dto.InstituicaoRequestDTO;
import br.com.fiap.sobrasolidaria.dto.InstituicaoResponseDTO;
import br.com.fiap.sobrasolidaria.dto.VinculoCategoriaRequestDTO;
import br.com.fiap.sobrasolidaria.exception.RecursoNaoEncontradoException;
import br.com.fiap.sobrasolidaria.exception.RegraNegocioException;
import br.com.fiap.sobrasolidaria.model.Categoria;
import br.com.fiap.sobrasolidaria.model.Instituicao;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/instituicoes")
public class InstituicaoController {

    private final InstituicaoDao instituicaoDao;
    private final CategoriaDao categoriaDao;
    private final RetiradaDao retiradaDao;
    private final ModelMapper mapper;

    public InstituicaoController(InstituicaoDao instituicaoDao, CategoriaDao categoriaDao,
                                 RetiradaDao retiradaDao, ModelMapper mapper) {
        this.instituicaoDao = instituicaoDao;
        this.categoriaDao = categoriaDao;
        this.retiradaDao = retiradaDao;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<InstituicaoResponseDTO> cadastrar(@Valid @RequestBody InstituicaoRequestDTO dto) {
        validarCnpjUnico(dto.getCnpj(), null);
        Instituicao instituicao = mapper.map(dto, Instituicao.class);
        instituicao.setDataCadastro(LocalDateTime.now());
        instituicaoDao.inserir(instituicao);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(instituicao.getId()).toUri();
        return ResponseEntity.created(location).body(paraResposta(instituicao));
    }

    @GetMapping
    public List<InstituicaoResponseDTO> listar() {
        return instituicaoDao.listar().stream().map(this::paraResposta).toList();
    }

    @GetMapping("/{id}")
    public InstituicaoResponseDTO buscarPorId(@PathVariable Long id) {
        return paraResposta(buscarOuFalhar(id));
    }

    @PutMapping("/{id}")
    public InstituicaoResponseDTO atualizar(@PathVariable Long id, @Valid @RequestBody InstituicaoRequestDTO dto) {
        Instituicao existente = buscarOuFalhar(id);
        validarCnpjUnico(dto.getCnpj(), id);

        existente.setNome(dto.getNome());
        existente.setCnpj(dto.getCnpj());
        existente.setEmail(dto.getEmail());
        existente.setEndereco(dto.getEndereco());
        existente.setPessoasAtendidas(dto.getPessoasAtendidas());
        instituicaoDao.atualizar(existente);
        return paraResposta(existente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        buscarOuFalhar(id);
        if (retiradaDao.possuiRetiradasDaInstituicao(id)) {
            throw new RegraNegocioException(
                    "Não é possível remover a instituição: ela possui retiradas registradas.");
        }
        instituicaoDao.remover(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------- Relacionamento N:N: categorias que a instituição aceita ----------------

    @GetMapping("/{id}/categorias")
    public List<CategoriaResponseDTO> listarCategorias(@PathVariable Long id) {
        buscarOuFalhar(id);
        return instituicaoDao.listarCategorias(id).stream()
                .map(c -> mapper.map(c, CategoriaResponseDTO.class)).toList();
    }

    @PostMapping("/{id}/categorias")
    public ResponseEntity<CategoriaResponseDTO> vincularCategoria(@PathVariable Long id,
                                                                  @Valid @RequestBody VinculoCategoriaRequestDTO dto) {
        buscarOuFalhar(id);
        Categoria categoria = categoriaDao.buscarPorId(dto.getCategoriaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Categoria " + dto.getCategoriaId() + " não encontrada."));
        if (instituicaoDao.existeVinculo(id, categoria.getId())) {
            throw new RegraNegocioException("A instituição já aceita esta categoria.");
        }
        instituicaoDao.vincular(id, categoria.getId());

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/categorias/{categoriaId}").buildAndExpand(categoria.getId()).toUri();
        return ResponseEntity.created(location).body(mapper.map(categoria, CategoriaResponseDTO.class));
    }

    @DeleteMapping("/{id}/categorias/{categoriaId}")
    public ResponseEntity<Void> desvincularCategoria(@PathVariable Long id, @PathVariable Long categoriaId) {
        buscarOuFalhar(id);
        if (!instituicaoDao.existeVinculo(id, categoriaId)) {
            throw new RecursoNaoEncontradoException("A instituição não possui vínculo com a categoria " + categoriaId + ".");
        }
        instituicaoDao.desvincular(id, categoriaId);
        return ResponseEntity.noContent().build();
    }

    private Instituicao buscarOuFalhar(Long id) {
        return instituicaoDao.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Instituição " + id + " não encontrada."));
    }

    private void validarCnpjUnico(String cnpj, Long ignorarId) {
        if (instituicaoDao.existePorCnpj(cnpj, ignorarId)) {
            throw new RegraNegocioException("Já existe uma instituição cadastrada com este CNPJ.");
        }
    }

    private InstituicaoResponseDTO paraResposta(Instituicao instituicao) {
        return mapper.map(instituicao, InstituicaoResponseDTO.class);
    }
}
