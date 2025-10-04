package br.com.fiap.checkpoint2.dao;

import br.com.fiap.checkpoint2.model.Produto;

import java.util.List;
import java.util.Optional;

public interface ProdutoDao {
    Produto cadastrar(Produto p);
    List<Produto> listar();
    Optional<Produto> pesquisarPorId(long id);
    boolean atualizar(Produto p);
    boolean remover(long id);

    // Requisito: pesquisa por atributo
    List<Produto> pesquisarPorNome(String termo);
    // Extra:
    List<Produto> pesquisarPorCategoria(String categoria);
}
