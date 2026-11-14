package br.com.fiap.sobrasolidaria.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import br.com.fiap.sobrasolidaria.model.StatusDoacao;

public class DoacaoResponseDTO {

    private Long id;
    private Long estabelecimentoId;
    private Long categoriaId;
    private String alimento;
    private String descricao;
    private BigDecimal quantidadeDisponivel;
    private String unidade;
    private LocalDate dataValidade;
    private StatusDoacao status;
    private LocalDateTime dataCadastro;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEstabelecimentoId() { return estabelecimentoId; }
    public void setEstabelecimentoId(Long estabelecimentoId) { this.estabelecimentoId = estabelecimentoId; }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    public String getAlimento() { return alimento; }
    public void setAlimento(String alimento) { this.alimento = alimento; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getQuantidadeDisponivel() { return quantidadeDisponivel; }
    public void setQuantidadeDisponivel(BigDecimal quantidadeDisponivel) { this.quantidadeDisponivel = quantidadeDisponivel; }

    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }

    public LocalDate getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDate dataValidade) { this.dataValidade = dataValidade; }

    public StatusDoacao getStatus() { return status; }
    public void setStatus(StatusDoacao status) { this.status = status; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
}
