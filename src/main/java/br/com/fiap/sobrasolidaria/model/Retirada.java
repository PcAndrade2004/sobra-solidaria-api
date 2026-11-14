package br.com.fiap.sobrasolidaria.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Registro de uma instituição que retirou (parte de) uma doação. */
public class Retirada {

    private Long id;
    private Long doacaoId;
    private Long instituicaoId;
    private BigDecimal quantidade;
    private String observacao;
    private LocalDateTime dataRetirada;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDoacaoId() { return doacaoId; }
    public void setDoacaoId(Long doacaoId) { this.doacaoId = doacaoId; }

    public Long getInstituicaoId() { return instituicaoId; }
    public void setInstituicaoId(Long instituicaoId) { this.instituicaoId = instituicaoId; }

    public BigDecimal getQuantidade() { return quantidade; }
    public void setQuantidade(BigDecimal quantidade) { this.quantidade = quantidade; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public LocalDateTime getDataRetirada() { return dataRetirada; }
    public void setDataRetirada(LocalDateTime dataRetirada) { this.dataRetirada = dataRetirada; }
}
