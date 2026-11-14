package br.com.fiap.sobrasolidaria.exception;

/** Regra de negócio violada ou remoção de registro com filhos. Vira HTTP 409. */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
