package br.com.fiap.sobrasolidaria.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import br.com.fiap.sobrasolidaria.model.Categoria;

@Repository
public class CategoriaDao {

    private static final String SEQUENCE = "sq_tb_categoria";
    static final String COLUNAS = "cd_categoria, nm_categoria, ds_categoria, fl_perecivel";

    private final DataSource dataSource;

    public CategoriaDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Categoria inserir(Categoria categoria) {
        String sql = "INSERT INTO tb_categoria (" + COLUNAS + ") VALUES (?, ?, ?, ?)";
        try (Connection conexao = dataSource.getConnection()) {
            long id = DaoSupport.proximoId(conexao, SEQUENCE);
            try (PreparedStatement ps = conexao.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.setString(2, categoria.getNome());
                ps.setString(3, categoria.getDescricao());
                ps.setString(4, categoria.getPerecivel() ? "S" : "N");
                ps.executeUpdate();
            }
            categoria.setId(id);
            return categoria;
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "inserir categoria");
        }
    }

    public List<Categoria> listar() {
        String sql = "SELECT " + COLUNAS + " FROM tb_categoria ORDER BY nm_categoria";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Categoria> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "listar categorias");
        }
    }

    public Optional<Categoria> buscarPorId(Long id) {
        String sql = "SELECT " + COLUNAS + " FROM tb_categoria WHERE cd_categoria = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "buscar categoria");
        }
    }

    public void atualizar(Categoria categoria) {
        String sql = "UPDATE tb_categoria SET nm_categoria = ?, ds_categoria = ?, fl_perecivel = ? "
                + "WHERE cd_categoria = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, categoria.getNome());
            ps.setString(2, categoria.getDescricao());
            ps.setString(3, categoria.getPerecivel() ? "S" : "N");
            ps.setLong(4, categoria.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "atualizar categoria");
        }
    }

    public void remover(Long id) {
        String sql = "DELETE FROM tb_categoria WHERE cd_categoria = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "remover categoria");
        }
    }

    /** Existe outra categoria com este nome? Em atualizações, ignora o próprio registro. */
    public boolean existePorNome(String nome, Long ignorarId) {
        String sql = "SELECT COUNT(*) FROM tb_categoria WHERE nm_categoria = ?"
                + (ignorarId != null ? " AND cd_categoria <> ?" : "");
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, nome);
            if (ignorarId != null) {
                ps.setLong(2, ignorarId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "verificar nome da categoria");
        }
    }

    /** Usado também pelo InstituicaoDao (listagem das categorias aceitas). */
    static Categoria mapear(ResultSet rs) throws SQLException {
        Categoria c = new Categoria();
        c.setId(rs.getLong("cd_categoria"));
        c.setNome(rs.getString("nm_categoria"));
        c.setDescricao(rs.getString("ds_categoria"));
        c.setPerecivel("S".equals(rs.getString("fl_perecivel")));
        return c;
    }
}
