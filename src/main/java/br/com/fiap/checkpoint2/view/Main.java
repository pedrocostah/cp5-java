package br.com.fiap.checkpoint2.view;

import br.com.fiap.checkpoint2.dao.ProdutoDao;
import br.com.fiap.checkpoint2.dao.ProdutoDaoImpl;
import br.com.fiap.checkpoint2.model.Produto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    private static final Scanner SC = new Scanner(System.in);
    private static final ProdutoDao dao = new ProdutoDaoImpl();

    public static void main(String[] args) {
        System.out.println("Java: " + System.getProperty("java.version"));
        System.out.println("==== Checkpoint 2 – Sistema de Estoque (Oracle) ====");
        boolean loop = true;
        while (loop) {
            try {
                menu();
                int op = lerInt("Escolha uma opção: ");
                switch (op) {
                    case 1 -> cadastrar();
                    case 2 -> listar();
                    case 3 -> pesquisarPorId();
                    case 4 -> atualizar();
                    case 5 -> remover();
                    case 6 -> pesquisarPorNome();       // Pesquisa por atributo (obrigatória)
                    case 7 -> pesquisarPorCategoria();  // Extra
                    case 0 -> { System.out.println("Saindo..."); loop = false; }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (RuntimeException e) {
                System.err.println("[ERRO] " + e.getMessage());
            } catch (Exception e) {
                System.err.println("[ERRO INESPERADO] " + e.getMessage());
            }
            System.out.println();
        }
        // encerra limpo e evita WARN do driver
        SC.close();
        System.exit(0);
    }

    private static void menu() {
        System.out.println("""
            ------------------------------
            1) Cadastrar
            2) Listar
            3) Pesquisar por ID
            4) Atualizar
            5) Remover
            6) Pesquisar por Atributo (Nome)
            7) Pesquisar por Categoria (extra)
            0) Sair
            ------------------------------""");
    }

    private static void cadastrar() {
        System.out.println("== Cadastrar Produto ==");
        String nome = lerTexto("Nome: ");
        String categoria = lerTexto("Categoria: ");
        BigDecimal preco = lerBigDecimal("Preço: ");
        int quantidade = lerIntNaoNegativo("Quantidade: ");
        String status = lerStatus("Status (ATIVO/INATIVO): ");

        Produto p = new Produto(nome, categoria, preco, quantidade, status);
        p = dao.cadastrar(p);
        System.out.println(">> Produto cadastrado! ID = " + p.getId());
    }

    private static void listar() {
        System.out.println("== Listar Produtos ==");
        List<Produto> lista = dao.listar();
        if (lista.isEmpty()) System.out.println("Nenhum produto.");
        else lista.forEach(System.out::println);
    }

    private static void pesquisarPorId() {
        System.out.println("== Pesquisar por ID ==");
        long id = lerLongPositivo("ID: ");
        Optional<Produto> opt = dao.pesquisarPorId(id);
        System.out.println(opt.map(Object::toString).orElse("Não encontrado."));
    }

    private static void atualizar() {
        System.out.println("== Atualizar Produto ==");
        long id = lerLongPositivo("ID: ");
        Optional<Produto> opt = dao.pesquisarPorId(id);
        if (opt.isEmpty()) { System.out.println("Não encontrado."); return; }
        Produto atual = opt.get();
        System.out.println("Atual: " + atual);

        String nome = lerTextoOpcional("Nome [" + atual.getNome() + "]: ", atual.getNome());
        String categoria = lerTextoOpcional("Categoria [" + atual.getCategoria() + "]: ", atual.getCategoria());
        BigDecimal preco = lerBigDecimalOpcional("Preço [" + atual.getPreco() + "]: ", atual.getPreco());
        int quantidade = lerIntOpcional("Quantidade [" + atual.getQuantidade() + "]: ", atual.getQuantidade());
        String status = lerStatusOpcional("Status (ATIVO/INATIVO) [" + atual.getStatus() + "]: ", atual.getStatus());

        Produto novo = new Produto(id, nome, categoria, preco, quantidade, status);
        boolean ok = dao.atualizar(novo);
        System.out.println(ok ? ">> Atualizado." : "Nada alterado.");
    }

    private static void remover() {
        System.out.println("== Remover Produto ==");
        long id = lerLongPositivo("ID: ");
        boolean ok = dao.remover(id);
        System.out.println(ok ? ">> Removido." : "Não encontrado.");
    }

    private static void pesquisarPorNome() {
        System.out.println("== Pesquisar por Nome ==");
        String termo = lerTexto("Contém: ");
        List<Produto> lista = dao.pesquisarPorNome(termo);
        if (lista.isEmpty()) System.out.println("Nenhum resultado.");
        else lista.forEach(System.out::println);
    }

    private static void pesquisarPorCategoria() {
        System.out.println("== Pesquisar por Categoria ==");
        String categoria = lerTexto("Categoria exata: ");
        List<Produto> lista = dao.pesquisarPorCategoria(categoria);
        if (lista.isEmpty()) {
            System.out.println("Nenhum resultado.");
        } else {
            lista.forEach(System.out::println);
        }
    }

    // ===== util =====
    private static String lerTexto(String msg) {
        System.out.print(msg);
        String s = SC.nextLine().trim();
        while (s.isEmpty()) { System.out.print("Obrigatório. " + msg); s = SC.nextLine().trim(); }
        return s;
    }

    private static String lerTextoOpcional(String msg, String padrao) {
        System.out.print(msg);
        String s = SC.nextLine().trim();
        return s.isEmpty() ? padrao : s;
    }

    private static int lerInt(String msg) {
        while (true) {
            System.out.print(msg);
            try { return Integer.parseInt(SC.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Inválido."); }
        }
    }

    private static int lerIntNaoNegativo(String msg) {
        int n;
        do { n = lerInt(msg); if (n < 0) System.out.println("Deve ser >= 0."); } while (n < 0);
        return n;
    }

    private static int lerIntOpcional(String msg, int padrao) {
        System.out.print(msg);
        String s = SC.nextLine().trim();
        if (s.isEmpty()) return padrao;
        try {
            int n = Integer.parseInt(s);
            if (n < 0) { System.out.println(">=0. Mantendo."); return padrao; }
            return n;
        } catch (NumberFormatException e) { System.out.println("Inválido. Mantendo."); return padrao; }
    }

    private static long lerLongPositivo(String msg) {
        while (true) {
            System.out.print(msg);
            try {
                long v = Long.parseLong(SC.nextLine().trim());
                if (v > 0) return v;
                System.out.println("> 0.");
            } catch (NumberFormatException e) { System.out.println("Inválido."); }
        }
    }

    private static BigDecimal lerBigDecimal(String msg) {
        while (true) {
            System.out.print(msg);
            String s = SC.nextLine().trim().replace(",", ".");
            try {
                BigDecimal v = new BigDecimal(s);
                if (v.signum() < 0) { System.out.println(">= 0."); continue; }
                return v;
            } catch (Exception e) { System.out.println("Ex.: 199.90"); }
        }
    }

    private static BigDecimal lerBigDecimalOpcional(String msg, BigDecimal padrao) {
        System.out.print(msg);
        String s = SC.nextLine().trim();
        if (s.isEmpty()) return padrao;
        s = s.replace(",", ".");
        try {
            BigDecimal v = new BigDecimal(s);
            if (v.signum() < 0) { System.out.println(">=0. Mantendo."); return padrao; }
            return v;
        } catch (Exception e) { System.out.println("Inválido. Mantendo."); return padrao; }
    }

    private static String lerStatus(String msg) {
        while (true) {
            String s = lerTexto(msg).toUpperCase();
            if (s.equals("ATIVO") || s.equals("INATIVO")) return s;
            System.out.println("Use ATIVO ou INATIVO.");
        }
    }

    private static String lerStatusOpcional(String msg, String padrao) {
        System.out.print(msg);
        String s = SC.nextLine().trim();
        if (s.isEmpty()) return padrao;
        s = s.toUpperCase();
        if (s.equals("ATIVO") || s.equals("INATIVO")) return s;
        System.out.println("Inválido. Mantendo.");
        return padrao;
    }
}
