package br.com.fiap.sobrasolidaria.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Corpo do POST /instituicoes/{id}/categorias (relacionamento N:N). */
public class VinculoCategoriaRequestDTO {

    @NotNull(message = "A categoria é obrigatória.")
    @Positive(message = "O id da categoria deve ser positivo.")
    private Long categoriaId;

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
}
