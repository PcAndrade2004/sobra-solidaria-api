package br.com.fiap.sobrasolidaria.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import br.com.fiap.sobrasolidaria.model.Categoria;
import br.com.fiap.sobrasolidaria.model.Instituicao;

@Repository
public class InstituicaoDao {

    private static final String SEQUENCE = "sq_tb_instituicao";
    private static final String COLUNAS =
            "cd_instituicao, nm_instituicao, nr_cnpj, ds_email, ds_endereco, qt_pessoas_atendidas, dt_cadastro";

    private final DataSource dataSource;

    public InstituicaoDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Instituicao inserir(Instituicao instituicao) {
        String sql = "INSERT INTO tb_instituicao (" + COLUNAS + ") VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conexao = dataSource.getConnection()) {
            long id = DaoSupport.proximoId(conexao, SEQUENCE);
            try (PreparedStatement ps = conexao.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.setString(2, instituicao.getNome());
                ps.setString(3, instituicao.getCnpj());
                ps.setString(4, instituicao.getEmail());
                ps.setString(5, instituicao.getEndereco());
                ps.setInt(6, instituicao.getPessoasAtendidas());
                ps.setTimestamp(7, Timestamp.valueOf(instituicao.getDataCadastro()));
                ps.executeUpdate();
            }
            instituicao.setId(id);
            return instituicao;
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "inserir instituição");
        }
    }

    public List<Instituicao> listar() {
        String sql = "SELECT " + COLUNAS + " FROM tb_instituicao ORDER BY cd_instituicao";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Instituicao> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "listar instituições");
        }
    }

    public Optional<Instituicao> buscarPorId(Long id) {
        String sql = "SELECT " + COLUNAS + " FROM tb_instituicao WHERE cd_instituicao = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "buscar instituição");
        }
    }

    public void atualizar(Instituicao instituicao) {
        String sql = "UPDATE tb_instituicao SET nm_instituicao = ?, nr_cnpj = ?, ds_email = ?, ds_endereco = ?, "
                + "qt_pessoas_atendidas = ? WHERE cd_instituicao = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, instituicao.getNome());
            ps.setString(2, instituicao.getCnpj());
            ps.setString(3, instituicao.getEmail());
            ps.setString(4, instituicao.getEndereco());
            ps.setInt(5, instituicao.getPessoasAtendidas());
            ps.setLong(6, instituicao.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "atualizar instituição");
        }
    }

    /** Remove a instituição e, na mesma transação, os vínculos da tabela associativa. */
    public void remover(Long id) {
        try (Connection conexao = dataSource.getConnection()) {
            conexao.setAutoCommit(false);
            try {
                executarDelete(conexao, "DELETE FROM tb_instituicao_categoria WHERE cd_instituicao = ?", id);
                executarDelete(conexao, "DELETE FROM tb_instituicao WHERE cd_instituicao = ?", id);
                conexao.commit();
            } catch (SQLException | RuntimeException e) {
                conexao.rollback();
                throw e;
            } finally {
                conexao.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "remover instituição");
        }
    }

    public boolean existePorCnpj(String cnpj, Long ignorarId) {
        String sql = "SELECT COUNT(*) FROM tb_instituicao WHERE nr_cnpj = ?"
                + (ignorarId != null ? " AND cd_instituicao <> ?" : "");
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
            throw DaoSupport.traduzir(e, "verificar CNPJ da instituição");
        }
    }

    // ---------------- Relacionamento N:N (tb_instituicao_categoria) ----------------

    public List<Categoria> listarCategorias(Long instituicaoId) {
        String sql = "SELECT c.cd_categoria, c.nm_categoria, c.ds_categoria, c.fl_perecivel "
                + "FROM tb_categoria c "
                + "INNER JOIN tb_instituicao_categoria ic ON ic.cd_categoria = c.cd_categoria "
                + "WHERE ic.cd_instituicao = ? ORDER BY c.nm_categoria";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, instituicaoId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Categoria> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(CategoriaDao.mapear(rs));
                }
                return lista;
            }
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "listar categorias da instituição");
        }
    }

    public boolean existeVinculo(Long instituicaoId, Long categoriaId) {
        String sql = "SELECT COUNT(*) FROM tb_instituicao_categoria WHERE cd_instituicao = ? AND cd_categoria = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, instituicaoId);
            ps.setLong(2, categoriaId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "verificar vínculo");
        }
    }

    public void vincular(Long instituicaoId, Long categoriaId) {
        String sql = "INSERT INTO tb_instituicao_categoria (cd_instituicao, cd_categoria, dt_vinculo) VALUES (?, ?, ?)";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, instituicaoId);
            ps.setLong(2, categoriaId);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "vincular categoria");
        }
    }

    public void desvincular(Long instituicaoId, Long categoriaId) {
        String sql = "DELETE FROM tb_instituicao_categoria WHERE cd_instituicao = ? AND cd_categoria = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, instituicaoId);
            ps.setLong(2, categoriaId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "desvincular categoria");
        }
    }

    /** Alguma instituição aceita esta categoria? (impede remover categoria em uso) */
    public boolean possuiVinculosDaCategoria(Long categoriaId) {
        String sql = "SELECT COUNT(*) FROM tb_instituicao_categoria WHERE cd_categoria = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, categoriaId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw DaoSupport.traduzir(e, "verificar vínculos da categoria");
        }
    }

    private void executarDelete(Connection conexao, String sql, Long id) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private Instituicao mapear(ResultSet rs) throws SQLException {
        Instituicao i = new Instituicao();
        i.setId(rs.getLong("cd_instituicao"));
        i.setNome(rs.getString("nm_instituicao"));
        i.setCnpj(rs.getString("nr_cnpj"));
        i.setEmail(rs.getString("ds_email"));
        i.setEndereco(rs.getString("ds_endereco"));
        i.setPessoasAtendidas(rs.getInt("qt_pessoas_atendidas"));
        i.setDataCadastro(rs.getTimestamp("dt_cadastro").toLocalDateTime());
        return i;
    }
}
