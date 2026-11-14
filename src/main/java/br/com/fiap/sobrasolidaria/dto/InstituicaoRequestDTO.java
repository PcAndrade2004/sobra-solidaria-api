package br.com.fiap.sobrasolidaria.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class InstituicaoRequestDTO {

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    private String nome;

    @NotBlank(message = "O CNPJ é obrigatório.")
    @Pattern(regexp = "\\d{14}", message = "O CNPJ deve ter 14 dígitos, apenas números.")
    private String cnpj;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "E-mail inválido.")
    @Size(max = 100, message = "O e-mail deve ter no máximo 100 caracteres.")
    private String email;

    @NotBlank(message = "O endereço é obrigatório.")
    @Size(max = 200, message = "O endereço deve ter no máximo 200 caracteres.")
    private String endereco;

    @NotNull(message = "A quantidade de pessoas atendidas é obrigatória.")
    @Min(value = 0, message = "A quantidade de pessoas atendidas não pode ser negativa.")
    @Max(value = 99999999, message = "A quantidade de pessoas atendidas é grande demais.")
    private Integer pessoasAtendidas;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public Integer getPessoasAtendidas() { return pessoasAtendidas; }
    public void setPessoasAtendidas(Integer pessoasAtendidas) { this.pessoasAtendidas = pessoasAtendidas; }
}
