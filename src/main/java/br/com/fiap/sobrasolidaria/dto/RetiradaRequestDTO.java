package br.com.fiap.sobrasolidaria.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class RetiradaRequestDTO {

    @NotNull(message = "A instituição é obrigatória.")
    @Positive(message = "O id da instituição deve ser positivo.")
    private Long instituicaoId;

    @NotNull(message = "A quantidade é obrigatória.")
    @Positive(message = "A quantidade deve ser maior que zero.")
    @Digits(integer = 8, fraction = 2, message = "A quantidade aceita até 8 dígitos inteiros e 2 decimais.")
    private BigDecimal quantidade;

    @Size(max = 300, message = "A observação deve ter no máximo 300 caracteres.")
    private String observacao;

    public Long getInstituicaoId() { return instituicaoId; }
    public void setInstituicaoId(Long instituicaoId) { this.instituicaoId = instituicaoId; }

    public BigDecimal getQuantidade() { return quantidade; }
    public void setQuantidade(BigDecimal quantidade) { this.quantidade = quantidade; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
