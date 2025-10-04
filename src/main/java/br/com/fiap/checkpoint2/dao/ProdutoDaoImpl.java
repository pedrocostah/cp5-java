package br.com.fiap.checkpoint2.dao;

import br.com.fiap.checkpoint2.factory.ConnectionFactory;
import br.com.fiap.checkpoint2.model.Produto;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProdutoDaoImpl implements ProdutoDao {

    @Override
    public Produto cadastrar(Produto p) {
        String sql = """
            INSERT INTO PRODUTO (ID, NOME, CATEGORIA, PRECO, QUANTIDADE, STATUS)
            VALUES (PRODUTO_SEQ.NEXTVAL, ?, ?, ?, ?, ?)
        """;

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNome());
            ps.setString(2, p.getCategoria());
            ps.setBigDecimal(3, p.getPreco());
            ps.setInt(4, p.getQuantidade());
            ps.setString(5, p.getStatus());
            ps.executeUpdate();

            // Pegar o ID gerado na mesma sessão
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT PRODUTO_SEQ.CURRVAL AS ID FROM DUAL")) {
                if (rs.next()) p.setId(rs.getLong("ID"));
            }

            return p;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar produto: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Produto> listar() {
        String sql = "SELECT ID, NOME, CATEGORIA, PRECO, QUANTIDADE, STATUS FROM PRODUTO ORDER BY ID";
        List<Produto> out = new ArrayList<>();
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Produto> pesquisarPorId(long id) {
        String sql = "SELECT ID, NOME, CATEGORIA, PRECO, QUANTIDADE, STATUS FROM PRODUTO WHERE ID = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao pesquisar por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean atualizar(Produto p) {
        String sql = """
            UPDATE PRODUTO SET NOME = ?, CATEGORIA = ?, PRECO = ?, QUANTIDADE = ?, STATUS = ?
            WHERE ID = ?
        """;
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getCategoria());
            ps.setBigDecimal(3, p.getPreco());
            ps.setInt(4, p.getQuantidade());
            ps.setString(5, p.getStatus());
            ps.setLong(6, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar produto: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean remover(long id) {
        String sql = "DELETE FROM PRODUTO WHERE ID = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover produto: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Produto> pesquisarPorNome(String termo) {
        String sql = """
            SELECT ID, NOME, CATEGORIA, PRECO, QUANTIDADE, STATUS
            FROM PRODUTO
            WHERE UPPER(NOME) LIKE UPPER(?)
            ORDER BY NOME
        """;
        List<Produto> out = new ArrayList<>();
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + termo + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao pesquisar por nome: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Produto> pesquisarPorCategoria(String categoria) {
        String sql = """
            SELECT ID, NOME, CATEGORIA, PRECO, QUANTIDADE, STATUS
            FROM PRODUTO
            WHERE UPPER(CATEGORIA) = UPPER(?)
            ORDER BY NOME
        """;
        List<Produto> out = new ArrayList<>();
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, categoria);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao pesquisar por categoria: " + e.getMessage(), e);
        }
    }

    private Produto map(ResultSet rs) throws SQLException {
        Long id = rs.getLong("ID");
        String nome = rs.getString("NOME");
        String categoria = rs.getString("CATEGORIA");
        BigDecimal preco = rs.getBigDecimal("PRECO");
        int quantidade = rs.getInt("QUANTIDADE");
        String status = rs.getString("STATUS");
        return new Produto(id, nome, categoria, preco, quantidade, status);
    }
}

