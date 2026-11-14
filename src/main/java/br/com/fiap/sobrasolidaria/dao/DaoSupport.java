package br.com.fiap.sobrasolidaria.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import br.com.fiap.sobrasolidaria.exception.ErroPersistenciaException;
import br.com.fiap.sobrasolidaria.exception.RegraNegocioException;

/** Utilitários compartilhados pelos DAOs. */
final class DaoSupport {

    private DaoSupport() {
    }

    /** Busca o próximo valor da sequence (o nome vem sempre de constantes dos DAOs, nunca do usuário). */
    static long proximoId(Connection conexao, String sequence) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement("SELECT " + sequence + ".NEXTVAL FROM dual");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        }
    }

    /**
     * Converte SQLException em exceção do domínio. Violação de integridade (SQLState 23xxx:
     * chave única, chave estrangeira...) vira conflito 409; o resto vira erro genérico 500.
     */
    static RuntimeException traduzir(SQLException e, String operacao) {
        String estado = e.getSQLState();
        if (estado != null && estado.startsWith("23")) {
            return new RegraNegocioException("A operação viola uma regra de integridade dos dados.");
        }
        return new ErroPersistenciaException("Falha ao " + operacao + " no banco de dados.", e);
    }
}
