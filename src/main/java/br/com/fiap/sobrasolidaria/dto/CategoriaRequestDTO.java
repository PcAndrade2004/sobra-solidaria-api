package br.com.fiap.sobrasolidaria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CategoriaRequestDTO {

    @NotBlank(message = "O nome da categoria é obrigatório.")
    @Size(max = 60, message = "O nome da categoria deve ter no máximo 60 caracteres.")
    private String nome;

    @Size(max = 200, message = "A descrição deve ter no máximo 200 caracteres.")
    private String descricao;

    @NotNull(message = "Informe se a categoria é perecível (true ou false).")
    private Boolean perecivel;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Boolean getPerecivel() { return perecivel; }
    public void setPerecivel(Boolean perecivel) { this.perecivel = perecivel; }
}
