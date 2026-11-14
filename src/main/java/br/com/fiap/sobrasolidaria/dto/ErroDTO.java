package br.com.fiap.sobrasolidaria.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Formato único de erro devolvido pela API. Nunca contém stack trace nem detalhes de SQL. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErroDTO(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho,
        List<CampoErroDTO> detalhes) {

    public record CampoErroDTO(String campo, String mensagem) {
    }
}
