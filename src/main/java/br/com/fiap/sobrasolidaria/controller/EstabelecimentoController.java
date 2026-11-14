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

import br.com.fiap.sobrasolidaria.dao.DoacaoDao;
import br.com.fiap.sobrasolidaria.dao.EstabelecimentoDao;
import br.com.fiap.sobrasolidaria.dto.DoacaoResponseDTO;
import br.com.fiap.sobrasolidaria.dto.EstabelecimentoRequestDTO;
import br.com.fiap.sobrasolidaria.dto.EstabelecimentoResponseDTO;
import br.com.fiap.sobrasolidaria.exception.RecursoNaoEncontradoException;
import br.com.fiap.sobrasolidaria.exception.RegraNegocioException;
import br.com.fiap.sobrasolidaria.model.Estabelecimento;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/estabelecimentos")
public class EstabelecimentoController {

    private final EstabelecimentoDao estabelecimentoDao;
    private final DoacaoDao doacaoDao;
    private final ModelMapper mapper;

    public EstabelecimentoController(EstabelecimentoDao estabelecimentoDao, DoacaoDao doacaoDao,
                                     ModelMapper mapper) {
        this.estabelecimentoDao = estabelecimentoDao;
        this.doacaoDao = doacaoDao;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<EstabelecimentoResponseDTO> cadastrar(@Valid @RequestBody EstabelecimentoRequestDTO dto) {
        validarCnpjUnico(dto.getCnpj(), null);

        Estabelecimento estabelecimento = mapper.map(dto, Estabelecimento.class);
        estabelecimento.setDataCadastro(LocalDateTime.now());
        estabelecimentoDao.inserir(estabelecimento);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(estabelecimento.getId()).toUri();
        return ResponseEntity.created(location).body(paraResposta(estabelecimento));
    }

    @GetMapping
    public List<EstabelecimentoResponseDTO> listar() {
        return estabelecimentoDao.listar().stream().map(this::paraResposta).toList();
    }

    @GetMapping("/{id}")
    public EstabelecimentoResponseDTO buscarPorId(@PathVariable Long id) {
        return paraResposta(buscarOuFalhar(id));
    }

    @PutMapping("/{id}")
    public EstabelecimentoResponseDTO atualizar(@PathVariable Long id,
                                                @Valid @RequestBody EstabelecimentoRequestDTO dto) {
        Estabelecimento existente = buscarOuFalhar(id);
        validarCnpjUnico(dto.getCnpj(), id);

        existente.setNome(dto.getNome());
        existente.setCnpj(dto.getCnpj());
        existente.setEmail(dto.getEmail());
        existente.setEndereco(dto.getEndereco());
        estabelecimentoDao.atualizar(existente);
        return paraResposta(existente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        buscarOuFalhar(id);
        if (doacaoDao.possuiDoacoes(id)) {
            throw new RegraNegocioException(
                    "Não é possível remover o estabelecimento: ele possui doações cadastradas.");
        }
        estabelecimentoDao.remover(id);
        return ResponseEntity.noContent().build();
    }

    /** Rota aninhada do relacionamento 1:N: doações de um estabelecimento. */
    @GetMapping("/{id}/doacoes")
    public List<DoacaoResponseDTO> listarDoacoes(@PathVariable Long id) {
        buscarOuFalhar(id);
        return doacaoDao.listarPorEstabelecimento(id).stream()
                .map(d -> mapper.map(d, DoacaoResponseDTO.class)).toList();
    }

    private Estabelecimento buscarOuFalhar(Long id) {
        return estabelecimentoDao.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estabelecimento " + id + " não encontrado."));
    }

    private void validarCnpjUnico(String cnpj, Long ignorarId) {
        if (estabelecimentoDao.existePorCnpj(cnpj, ignorarId)) {
            throw new RegraNegocioException("Já existe um estabelecimento cadastrado com este CNPJ.");
        }
    }

    private EstabelecimentoResponseDTO paraResposta(Estabelecimento estabelecimento) {
        return mapper.map(estabelecimento, EstabelecimentoResponseDTO.class);
    }
}
