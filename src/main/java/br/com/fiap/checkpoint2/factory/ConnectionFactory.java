package br.com.fiap.checkpoint2.factory;

import java.io.Console;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class ConnectionFactory {

    // Padrão (SID ORCL). Se sua conexão for SERVICE NAME, passe na variável DB_URL (ver README abaixo).
    private static final String DEFAULT_URL = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL";

    private static String URL;
    private static String USER;
    private static String PASS;

    static {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver Oracle JDBC não encontrado no classpath.", e);
        }

        // 1) System properties (-Ddb.*) OU variáveis de ambiente (DB_*)
        URL  = firstNonBlank(System.getProperty("db.url"),  System.getenv("DB_URL"),  DEFAULT_URL);
        USER = firstNonBlank(System.getProperty("db.user"), System.getenv("DB_USER"), null);
        PASS = firstNonBlank(System.getProperty("db.pass"), System.getenv("DB_PASS"), null);

        // 2) Se faltar, perguntar no console (último recurso)
        if (isBlank(USER) || isBlank(PASS)) {
            Console console = System.console();
            if (console != null) {
                if (isBlank(USER)) USER = trimToNull(console.readLine("Usuário Oracle: "));
                if (isBlank(PASS)) PASS = new String(console.readPassword("Senha Oracle: "));
            } else {
                Scanner sc = new Scanner(System.in);
                if (isBlank(USER)) { System.out.print("Usuário Oracle: "); USER = trimToNull(sc.nextLine()); }
                if (isBlank(PASS)) { System.out.print("Senha Oracle: ");   PASS = trimToNull(sc.nextLine()); }
            }
        }

        if (isBlank(USER) || isBlank(PASS)) {
            throw new RuntimeException("Credenciais ausentes. Defina DB_USER/DB_PASS (ou -Ddb.user/-Ddb.pass).");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    private static String firstNonBlank(String a, String b, String c) {
        if (!isBlank(a)) return a;
        if (!isBlank(b)) return b;
        return c;
    }
    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
