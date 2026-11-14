package br.com.fiap.sobrasolidaria.exception;

/**
 * Falha inesperada de banco de dados. A causa (SQLException) fica só no log;
 * o cliente recebe uma mensagem genérica com HTTP 500.
 */
public class ErroPersistenciaException extends RuntimeException {

    public ErroPersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
