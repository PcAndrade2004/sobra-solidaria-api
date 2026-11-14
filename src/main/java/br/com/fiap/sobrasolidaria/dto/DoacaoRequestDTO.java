package br.com.fiap.sobrasolidaria.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class DoacaoRequestDTO {

    @NotNull(message = "O estabelecimento é obrigatório.")
    @Positive(message = "O id do estabelecimento deve ser positivo.")
    private Long estabelecimentoId;

    @NotNull(message = "A categoria é obrigatória.")
    @Positive(message = "O id da categoria deve ser positivo.")
    private Long categoriaId;

    @NotBlank(message = "O nome do alimento é obrigatório.")
    @Size(max = 100, message = "O nome do alimento deve ter no máximo 100 caracteres.")
    private String alimento;

    @Size(max = 300, message = "A descrição deve ter no máximo 300 caracteres.")
    private String descricao;

    @NotNull(message = "A quantidade é obrigatória.")
    @Positive(message = "A quantidade deve ser maior que zero.")
    @Digits(integer = 8, fraction = 2, message = "A quantidade aceita até 8 dígitos inteiros e 2 decimais.")
    private BigDecimal quantidadeDisponivel;

    @NotBlank(message = "A unidade é obrigatória (ex.: kg, un, L).")
    @Size(max = 20, message = "A unidade deve ter no máximo 20 caracteres.")
    private String unidade;

    @NotNull(message = "A data de validade é obrigatória.")
    @Future(message = "A data de validade deve ser futura.")
    private LocalDate dataValidade;

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
}
