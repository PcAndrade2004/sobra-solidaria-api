package br.com.fiap.sobrasolidaria.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.fiap.sobrasolidaria.dao.DoacaoDao;
import br.com.fiap.sobrasolidaria.dao.InstituicaoDao;
import br.com.fiap.sobrasolidaria.dao.RetiradaDao;
import br.com.fiap.sobrasolidaria.dto.RetiradaRequestDTO;
import br.com.fiap.sobrasolidaria.dto.RetiradaResponseDTO;
import br.com.fiap.sobrasolidaria.exception.RecursoNaoEncontradoException;
import br.com.fiap.sobrasolidaria.model.Retirada;
import jakarta.validation.Valid;

/** Rotas aninhadas do relacionamento 1:N: retiradas de uma doação. */
@RestController
@RequestMapping("/doacoes/{doacaoId}/retiradas")
public class RetiradaController {

    private final RetiradaDao retiradaDao;
    private final DoacaoDao doacaoDao;
    private final InstituicaoDao instituicaoDao;
    private final ModelMapper mapper;

    public RetiradaController(RetiradaDao retiradaDao, DoacaoDao doacaoDao, InstituicaoDao instituicaoDao,
                              ModelMapper mapper) {
        this.retiradaDao = retiradaDao;
        this.doacaoDao = doacaoDao;
        this.instituicaoDao = instituicaoDao;
        this.mapper = mapper;
    }

    @GetMapping
    public List<RetiradaResponseDTO> listar(@PathVariable Long doacaoId) {
        garantirDoacao(doacaoId);
        return retiradaDao.listarPorDoacao(doacaoId).stream().map(this::paraResposta).toList();
    }

    @GetMapping("/{retiradaId}")
    public RetiradaResponseDTO buscarPorId(@PathVariable Long doacaoId, @PathVariable Long retiradaId) {
        garantirDoacao(doacaoId);
        return retiradaDao.buscarPorId(doacaoId, retiradaId)
                .map(this::paraResposta)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Retirada " + retiradaId + " não encontrada."));
    }

    @PostMapping
    public ResponseEntity<RetiradaResponseDTO> registrar(@PathVariable Long doacaoId,
                                                         @Valid @RequestBody RetiradaRequestDTO dto) {
        if (instituicaoDao.buscarPorId(dto.getInstituicaoId()).isEmpty()) {
            throw new RecursoNaoEncontradoException("Instituição " + dto.getInstituicaoId() + " não encontrada.");
        }
        Retirada retirada = mapper.map(dto, Retirada.class);
        retirada.setDataRetirada(LocalDateTime.now());
        retiradaDao.registrar(doacaoId, retirada);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(retirada.getId()).toUri();
        return ResponseEntity.created(location).body(paraResposta(retirada));
    }

    private void garantirDoacao(Long doacaoId) {
        if (doacaoDao.buscarPorId(doacaoId).isEmpty()) {
            throw new RecursoNaoEncontradoException("Doação " + doacaoId + " não encontrada.");
        }
    }

    private RetiradaResponseDTO paraResposta(Retirada retirada) {
        return mapper.map(retirada, RetiradaResponseDTO.class);
    }
}
