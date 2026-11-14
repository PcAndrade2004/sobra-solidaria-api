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

import br.com.fiap.sobrasolidaria.model.Estabelecimento;

@Repository
public class EstabelecimentoDao {

    private static final String SEQUENCE = "sq_tb_estabelecimento";
    private static final String COLUNAS =
            "cd_estabelecimento, nm_estabelecimento, nr_cnpj, ds_email, ds_endereco, dt_cadastro";

    private final DataSource dataSource;

    public EstabelecimentoDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Estabelecimento inserir(Estabelecimento estabelecimento) {
        String sql = "INSERT INTO tb_estabelecimento (" + COLUNAS + ") VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conexao = dataSource.getConnection()) {
            long id = DaoSupport.proximoId(conexao, SEQUENCE);
            try (PreparedStatement ps = conexao.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.setString(2, estabelecimento.getNome());
                ps.setString(3, estabelecimento.getCnpj());
                ps.setString(4, estabelecimento.getEmail());
                ps.setString(5, estabelecimento.getEndereco());
                ps.setTimestamp(6, Timestamp.valueOf(estabelecimento.getDataCadastro()));
                ps.executeUpdate();
            }
            estabelecimento.setId(id);
            return estabelecimento;
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "inserir estabelecimento");
        }
    }

    public List<Estabelecimento> listar() {
        String sql = "SELECT " + COLUNAS + " FROM tb_estabelecimento ORDER BY cd_estabelecimento";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Estabelecimento> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "listar estabelecimentos");
        }
    }

    public Optional<Estabelecimento> buscarPorId(Long id) {
        String sql = "SELECT " + COLUNAS + " FROM tb_estabelecimento WHERE cd_estabelecimento = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "buscar estabelecimento");
        }
    }

    public void atualizar(Estabelecimento estabelecimento) {
        String sql = "UPDATE tb_estabelecimento SET nm_estabelecimento = ?, nr_cnpj = ?, ds_email = ?, "
                + "ds_endereco = ? WHERE cd_estabelecimento = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, estabelecimento.getNome());
            ps.setString(2, estabelecimento.getCnpj());
            ps.setString(3, estabelecimento.getEmail());
            ps.setString(4, estabelecimento.getEndereco());
            ps.setLong(5, estabelecimento.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "atualizar estabelecimento");
        }
    }

    public void remover(Long id) {
        String sql = "DELETE FROM tb_estabelecimento WHERE cd_estabelecimento = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "remover estabelecimento");
        }
    }

    /** Existe outro estabelecimento com este CNPJ? Em atualizações, ignora o próprio registro. */
    public boolean existePorCnpj(String cnpj, Long ignorarId) {
        String sql = "SELECT COUNT(*) FROM tb_estabelecimento WHERE nr_cnpj = ?"
                + (ignorarId != null ? " AND cd_estabelecimento <> ?" : "");
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, cnpj);
            if (ignorarId != null) {
                ps.setLong(2, ignorarId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "verificar CNPJ");
        }
    }

    private Estabelecimento mapear(ResultSet rs) throws SQLException {
        Estabelecimento e = new Estabelecimento();
        e.setId(rs.getLong("cd_estabelecimento"));
        e.setNome(rs.getString("nm_estabelecimento"));
        e.setCnpj(rs.getString("nr_cnpj"));
        e.setEmail(rs.getString("ds_email"));
        e.setEndereco(rs.getString("ds_endereco"));
        e.setDataCadastro(rs.getTimestamp("dt_cadastro").toLocalDateTime());
        return e;
    }
}
