package br.com.fiap.sobrasolidaria.exception;

/** Recurso inexistente. Vira HTTP 404. */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
