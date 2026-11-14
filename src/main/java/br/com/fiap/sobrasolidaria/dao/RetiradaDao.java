package br.com.fiap.sobrasolidaria.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import br.com.fiap.sobrasolidaria.exception.RecursoNaoEncontradoException;
import br.com.fiap.sobrasolidaria.model.Doacao;
import br.com.fiap.sobrasolidaria.model.Retirada;

@Repository
public class RetiradaDao {

    private static final String SEQUENCE = "sq_tb_retirada";
    private static final String COLUNAS =
            "cd_retirada, cd_doacao, cd_instituicao, qt_retirada, ds_observacao, dt_retirada";

    private final DataSource dataSource;
    private final DoacaoDao doacaoDao;

    public RetiradaDao(DataSource dataSource, DoacaoDao doacaoDao) {
        this.dataSource = dataSource;
        this.doacaoDao = doacaoDao;
    }

    /**
     * Registra a retirada e baixa a quantidade da doação na mesma transação.
     * A linha da doação é travada (FOR UPDATE) para que duas retiradas simultâneas
     * não consumam a mesma quantidade. Se a regra de negócio falhar, nada é gravado.
     */
    public Retirada registrar(Long doacaoId, Retirada retirada) {
        String sql = "INSERT INTO tb_retirada (" + COLUNAS + ") VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conexao = dataSource.getConnection()) {
            conexao.setAutoCommit(false);
            try {
                Doacao doacao = doacaoDao.buscarPorId(conexao, doacaoId, true)
                        .orElseThrow(() -> new RecursoNaoEncontradoException(
                                "Doação " + doacaoId + " não encontrada."));

                doacao.registrarRetirada(retirada.getQuantidade());

                long id = DaoSupport.proximoId(conexao, SEQUENCE);
                try (PreparedStatement ps = conexao.prepareStatement(sql)) {
                    ps.setLong(1, id);
                    ps.setLong(2, doacaoId);
                    ps.setLong(3, retirada.getInstituicaoId());
                    ps.setBigDecimal(4, retirada.getQuantidade());
                    ps.setString(5, retirada.getObservacao());
                    ps.setTimestamp(6, Timestamp.valueOf(retirada.getDataRetirada()));
                    ps.executeUpdate();
                }
                doacaoDao.atualizarDisponibilidade(conexao, doacao);

                conexao.commit();
                retirada.setId(id);
                retirada.setDoacaoId(doacaoId);
                return retirada;
            } catch (SQLException | RuntimeException e) {
                conexao.rollback();
                throw e;
            } finally {
                conexao.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "registrar retirada");
        }
    }

    public List<Retirada> listarPorDoacao(Long doacaoId) {
        String sql = "SELECT " + COLUNAS + " FROM tb_retirada WHERE cd_doacao = ? ORDER BY cd_retirada";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, doacaoId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Retirada> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
                return lista;
            }
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "listar retiradas");
        }
    }

    public Optional<Retirada> buscarPorId(Long doacaoId, Long retiradaId) {
        String sql = "SELECT " + COLUNAS + " FROM tb_retirada WHERE cd_doacao = ? AND cd_retirada = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, doacaoId);
            ps.setLong(2, retiradaId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "buscar retirada");
        }
    }

    public boolean possuiRetiradas(Long doacaoId) {
        String sql = "SELECT COUNT(*) FROM tb_retirada WHERE cd_doacao = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, doacaoId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "verificar retiradas da doação");
        }
    }

    public boolean possuiRetiradasDaInstituicao(Long instituicaoId) {
        String sql = "SELECT COUNT(*) FROM tb_retirada WHERE cd_instituicao = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, instituicaoId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "verificar retiradas da instituição");
        }
    }

    private Retirada mapear(ResultSet rs) throws SQLException {
        Retirada r = new Retirada();
        r.setId(rs.getLong("cd_retirada"));
        r.setDoacaoId(rs.getLong("cd_doacao"));
        r.setInstituicaoId(rs.getLong("cd_instituicao"));
        r.setQuantidade(rs.getBigDecimal("qt_retirada"));
        r.setObservacao(rs.getString("ds_observacao"));
        r.setDataRetirada(rs.getTimestamp("dt_retirada").toLocalDateTime());
        return r;
    }
}
