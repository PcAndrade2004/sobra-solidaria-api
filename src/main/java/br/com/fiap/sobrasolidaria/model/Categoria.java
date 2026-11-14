package br.com.fiap.sobrasolidaria.model;

/** Categoria de alimento (paes, hortifruti, refeições prontas...). */
public class Categoria {

    private Long id;
    private String nome;
    private String descricao;
    private Boolean perecivel;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Boolean getPerecivel() { return perecivel; }
    public void setPerecivel(Boolean perecivel) { this.perecivel = perecivel; }
}
