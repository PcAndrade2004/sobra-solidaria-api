package br.com.fiap.sobrasolidaria.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import br.com.fiap.sobrasolidaria.model.Doacao;
import br.com.fiap.sobrasolidaria.model.StatusDoacao;

@Repository
public class DoacaoDao {

    private static final String SEQUENCE = "sq_tb_doacao";
    private static final String COLUNAS = "cd_doacao, cd_estabelecimento, cd_categoria, nm_alimento, ds_alimento, "
            + "qt_disponivel, ds_unidade, dt_validade, st_doacao, dt_cadastro";

    private final DataSource dataSource;

    public DoacaoDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Doacao inserir(Doacao doacao) {
        String sql = "INSERT INTO tb_doacao (" + COLUNAS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conexao = dataSource.getConnection()) {
            long id = DaoSupport.proximoId(conexao, SEQUENCE);
            try (PreparedStatement ps = conexao.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.setLong(2, doacao.getEstabelecimentoId());
                ps.setLong(3, doacao.getCategoriaId());
                ps.setString(4, doacao.getAlimento());
                ps.setString(5, doacao.getDescricao());
                ps.setBigDecimal(6, doacao.getQuantidadeDisponivel());
                ps.setString(7, doacao.getUnidade());
                ps.setDate(8, Date.valueOf(doacao.getDataValidade()));
                ps.setString(9, doacao.getStatus().name());
                ps.setTimestamp(10, Timestamp.valueOf(doacao.getDataCadastro()));
                ps.executeUpdate();
            }
            doacao.setId(id);
            return doacao;
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "inserir doação");
        }
    }

    /** Lista todas as doações; se {@code status} não for nulo, filtra por ele. */
    public List<Doacao> listar(StatusDoacao status) {
        String sql = "SELECT " + COLUNAS + " FROM tb_doacao"
                + (status != null ? " WHERE st_doacao = ?" : "")
                + " ORDER BY dt_validade, cd_doacao";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            if (status != null) {
                ps.setString(1, status.name());
            }
            return lerLista(ps);
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "listar doações");
        }
    }

    public List<Doacao> listarPorEstabelecimento(Long estabelecimentoId) {
        String sql = "SELECT " + COLUNAS + " FROM tb_doacao WHERE cd_estabelecimento = ? "
                + "ORDER BY dt_validade, cd_doacao";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, estabelecimentoId);
            return lerLista(ps);
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "listar doações do estabelecimento");
        }
    }

    public Optional<Doacao> buscarPorId(Long id) {
        try (Connection conexao = dataSource.getConnection()) {
            return buscarPorId(conexao, id, false);
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "buscar doação");
        }
    }

    public void atualizar(Doacao doacao) {
        String sql = "UPDATE tb_doacao SET cd_estabelecimento = ?, cd_categoria = ?, nm_alimento = ?, ds_alimento = ?, "
                + "qt_disponivel = ?, ds_unidade = ?, dt_validade = ?, st_doacao = ? WHERE cd_doacao = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, doacao.getEstabelecimentoId());
            ps.setLong(2, doacao.getCategoriaId());
            ps.setString(3, doacao.getAlimento());
            ps.setString(4, doacao.getDescricao());
            ps.setBigDecimal(5, doacao.getQuantidadeDisponivel());
            ps.setString(6, doacao.getUnidade());
            ps.setDate(7, Date.valueOf(doacao.getDataValidade()));
            ps.setString(8, doacao.getStatus().name());
            ps.setLong(9, doacao.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "atualizar doação");
        }
    }

    public void remover(Long id) {
        String sql = "DELETE FROM tb_doacao WHERE cd_doacao = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "remover doação");
        }
    }

    public boolean possuiDoacoes(Long estabelecimentoId) {
        String sql = "SELECT COUNT(*) FROM tb_doacao WHERE cd_estabelecimento = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, estabelecimentoId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "verificar doações do estabelecimento");
        }
    }

    public boolean possuiDoacoesDaCategoria(Long categoriaId) {
        String sql = "SELECT COUNT(*) FROM tb_doacao WHERE cd_categoria = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, categoriaId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "verificar doações da categoria");
        }
    }

    // ---- Métodos usados pelo RetiradaDao dentro de uma transação (mesma conexão) ----

    /** Busca a doação na conexão informada; com {@code travar} faz SELECT ... FOR UPDATE. */
    Optional<Doacao> buscarPorId(Connection conexao, Long id, boolean travar) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM tb_doacao WHERE cd_doacao = ?" + (travar ? " FOR UPDATE" : "");
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    void atualizarDisponibilidade(Connection conexao, Doacao doacao) throws SQLException {
        String sql = "UPDATE tb_doacao SET qt_disponivel = ?, st_doacao = ? WHERE cd_doacao = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setBigDecimal(1, doacao.getQuantidadeDisponivel());
            ps.setString(2, doacao.getStatus().name());
            ps.setLong(3, doacao.getId());
            ps.executeUpdate();
        }
    }

    private List<Doacao> lerLista(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            List<Doacao> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        }
    }

    private Doacao mapear(ResultSet rs) throws SQLException {
        Doacao d = new Doacao();
        d.setId(rs.getLong("cd_doacao"));
        d.setEstabelecimentoId(rs.getLong("cd_estabelecimento"));
        d.setCategoriaId(rs.getLong("cd_categoria"));
        d.setAlimento(rs.getString("nm_alimento"));
        d.setDescricao(rs.getString("ds_alimento"));
        d.setQuantidadeDisponivel(rs.getBigDecimal("qt_disponivel"));
        d.setUnidade(rs.getString("ds_unidade"));
        d.setDataValidade(rs.getDate("dt_validade").toLocalDate());
        d.setStatus(StatusDoacao.valueOf(rs.getString("st_doacao")));
        d.setDataCadastro(rs.getTimestamp("dt_cadastro").toLocalDateTime());
        return d;
    }
}
