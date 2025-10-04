README.md
<div align="center">

# ✅ Checkpoint 2 — Sistema de Estoque (Java 21 + Oracle JDBC)

**Console App** (CLI) em **Java 21** com **Maven**, arquitetura **Model / DAO / View**, persistência em **Oracle** via JDBC  
e **segurança de credenciais** (sem usuário/senha no código ou no repositório).

</div>

---

## 📌 Sumário

- [Visão Geral](#-visão-geral)
- [Stack Tecnológico](#-stack-tecnológico)
- [Arquitetura & Padrões](#-arquitetura--padrões)
- [Estrutura de Pastas](#-estrutura-de-pastas)
- [Banco de Dados (Oracle)](#-banco-de-dados-oracle)
- [Como Rodar (2 minutos)](#-como-rodar-2-minutos)
  - [Windows (PowerShell)](#windows-powershell)
  - [Linux/macOS (bash/zsh)](#linuxmacos-bashzsh)
  - [IntelliJ IDEA (opcional)](#intellij-idea-opcional)
- [Uso — Menu da Aplicação](#-uso--menu-da-aplicação)
- [Segurança (Sem credenciais no repo)](#-segurança-sem-credenciais-no-repo)
- [Solução de Problemas](#-solução-de-problemas)
- [Checklist de Avaliação (Requisitos do PDF)](#-checklist-de-avaliação-requisitos-do-pdf)
- [Licença](#-licença)

---

## 🧭 Visão Geral

Este projeto implementa um **Sistema de Estoque** simples para gerenciar **Produtos** com as operações:

- **CRUD completo**: Cadastrar, Listar, Pesquisar por ID, Atualizar e Remover  
- **Pesquisa por atributo**: buscar por **Nome** (contém)  
- **Pesquisa extra**: buscar por **Categoria** (igual)  
- **Validações**: `preço ≥ 0`, `quantidade ≥ 0`, `status ∈ {ATIVO, INATIVO}`

Foi desenvolvido para **Oracle SQL Developer** com **JDK 21 (Oracle 21.0.7)** e **Maven 3.9+**.

---

## 🧰 Stack Tecnológico

- **Java 21 (Oracle JDK 21.0.7)**  
- **Maven** (compilação e execução)
- **Oracle Database** (acesso via JDBC)
- **Driver JDBC**: `com.oracle.database.jdbc:ojdbc11:23.4.0.24.05`

---

## 🧱 Arquitetura & Padrões

- **Model / DAO / View**:
  - **model**: entidades de domínio (`Produto`)
  - **dao**: interface + implementação com JDBC (`ProdutoDao`, `ProdutoDaoImpl`)
  - **factory**: criação de conexões (`ConnectionFactory`)
  - **view**: camada de apresentação (CLI / `Main`)
- **JDBC** com `PreparedStatement` e **Mapeamento** explícito de `ResultSet`
- **Tratamento de exceções** com mensagens claras
- **Configuração de credenciais em runtime** (sem hard-code)

---

## 🗂 Estrutura de Pastas



src/
main/
java/br/com/fiap/checkpoint2/
model/ -> Produto.java
dao/ -> ProdutoDao.java, ProdutoDaoImpl.java
factory/ -> ConnectionFactory.java
view/ -> Main.java
resources/
sql/ -> schema_oracle.sql (DDL + seed)
db.properties.example (apenas referência; não usado em runtime)
pom.xml


> **Observação**: a pasta `resources/sql` contém o script que o avaliador executa no **SQL Developer** para criar a tabela/índices/sequence.

---

## 🛢 Banco de Dados (Oracle)

- **Script DDL**: `src/main/resources/sql/schema_oracle.sql`
  - Tabela: `PRODUTO (ID, NOME, CATEGORIA, PRECO, QUANTIDADE, STATUS)`
  - **Restrições**: preço ≥ 0, quantidade ≥ 0, status em {ATIVO, INATIVO}
  - Índices: `IX_PRODUTO_NOME`, `IX_PRODUTO_CATEGORIA`
  - Sequence: `PRODUTO_SEQ` para gerar `ID`
  - Seed opcional: 2 registros (Teclado, Mouse)

- **URL de conexão** (ajuste conforme seu SQL Developer):
  - **SID**: `jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL`
  - **Service Name**: `jdbc:oracle:thin:@//oracle.fiap.com.br:1521/ORCL`

> **Importante**: execute o DDL **no mesmo usuário** com o qual você vai rodar a aplicação.

---

## ⚡ Como Rodar (2 minutos)

### Windows (PowerShell)

1. **Verifique Java/Maven**  
   ```powershell
   mvn -v   # precisa mostrar "Java version: 21..."


Caso não mostre 21, faça:

$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:Path      = "$env:JAVA_HOME\bin;$env:Path"
mvn -v


Crie o schema no Oracle (no SQL Developer, conectado com seu usuário)
Execute src/main/resources/sql/schema_oracle.sql.

Execute a aplicação (vai pedir usuário/senha no console)

mvn clean package
mvn exec:java@run-main


Se sua conexão for Service Name, você pode informar a URL na hora de rodar:

mvn -Ddb.url="jdbc:oracle:thin:@//oracle.fiap.com.br:1521/ORCL" exec:java@run-main


Opcional (sem digitar sempre) — use variáveis de ambiente:

$env:DB_URL  = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL"  # ou @//.../ORCL
$env:DB_USER = "SEU_RM"
$env:DB_PASS = "SUA_SENHA"
mvn exec:java@run-main

Linux/macOS (bash/zsh)
# Java 21
java -version
# Caso necessário:
export JAVA_HOME=/usr/lib/jvm/jdk-21            # ajuste para seu caminho
export PATH="$JAVA_HOME/bin:$PATH"

# Criar schema no Oracle (rodar o SQL no seu usuário via SQL Developer/SQL*Plus)

# Executar
mvn clean package
mvn exec:java@run-main

# Opcional: variáveis de ambiente
export DB_URL="jdbc:oracle:thin:@//oracle.fiap.com.br:1521/ORCL"
export DB_USER="SEU_RM"
export DB_PASS="SUA_SENHA"
mvn exec:java@run-main

IntelliJ IDEA (opcional)

Project SDK: JDK 21

Language level: 21

Maven → Use Project JDK

Run Configuration (Maven): objetivo exec:java@run-main

Se quiser, defina DB_URL/DB_USER/DB_PASS na aba Environment variables da Run Configuration

Janela Maven → Reload All Maven Projects (ícone com duas setas)

🖱 Uso — Menu da Aplicação
------------------------------
1) Cadastrar
2) Listar
3) Pesquisar por ID
4) Atualizar
5) Remover
6) Pesquisar por Atributo (Nome)
7) Pesquisar por Categoria (extra)
0) Sair
------------------------------


Cadastrar: informa nome, categoria, preço, quantidade, status (ATIVO/INATIVO)

Listar: mostra todos os produtos

Pesquisar por ID: exibe o produto ou “Não encontrado”

Atualizar: carrega dados atuais e permite trocar campos individualmente

Remover: deleta pelo ID

Pesquisar por Nome: LIKE '%termo%' (case-insensitive)

Pesquisar por Categoria: igualdade (case-insensitive)

Validações:

preço ≥ 0

quantidade ≥ 0

status: ATIVO ou INATIVO

🔐 Segurança (Sem credenciais no repo)

Não há db.properties com senha no projeto.

O app obtém credenciais, nesta ordem:

Parâmetros Maven: -Ddb.url, -Ddb.user, -Ddb.pass

Variáveis de ambiente: DB_URL, DB_USER, DB_PASS

Prompt no console (pede usuário/senha em runtime)

Exemplos:

# Prompt (mais seguro para demonstrar)
mvn exec:java@run-main

# Variáveis de ambiente
$env:DB_URL  = "jdbc:oracle:thin:@//oracle.fiap.com.br:1521/ORCL"
$env:DB_USER = "RM123456"
$env:DB_PASS = "MinhaSenhaForte!"
mvn exec:java@run-main

# Parâmetros Maven
mvn -Ddb.url="jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL" `
    -Ddb.user=RM123456 -Ddb.pass=MinhaSenhaForte! `
    exec:java@run-main

🛟 Solução de Problemas
ORA-01017: invalid username/password; logon denied

Usuário/senha incorretos ou URL não corresponde ao tipo de conexão (SID x Service Name).

Confirme que você entra no SQL Developer com as mesmas credenciais.

Se no SQL Developer usa Service Name, então a URL correta é @//host:1521/ORCL.

Dica: remova variáveis e deixe o app perguntar no console:

Remove-Item Env:DB_USER, Env:DB_PASS, Env:DB_URL -ErrorAction SilentlyContinue
mvn exec:java@run-main

release 21 not supported

O Maven está usando um JDK antigo.
Solução:

$env:JAVA_HOME="C:\Program Files\Java\jdk-21"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
mvn -v  # precisa mostrar Java 21

ClassNotFoundException: br.com.fiap.checkpoint2.view.Main

A compilação falhou antes. Rode:

mvn clean package
mvn exec:java@run-main

Acentos viram ? no Windows

Use UTF-8 no console:

chcp 65001

Avisos ao sair (threads do Oracle)

O projeto já configura o exec-maven-plugin com <cleanupDaemonThreads>false</cleanupDaemonThreads>,
e a Main chama System.exit(0) — não deve aparecer.

✅ Checklist de Avaliação (Requisitos do PDF)

 JDK 21 + Maven configurados (pom.xml com maven-compiler-plugin, exec-maven-plugin)

 Arquitetura em camadas: model, dao, view (+ factory)

 CRUD completo (insert, select, update, delete)

 Pesquisa por atributo (nome via LIKE)

 Pesquisa extra (categoria)

 Validações (preço/quantidade/status)

 JDBC Oracle (driver ojdbc11 no pom.xml)

 Script SQL (schema_oracle.sql) com tabela + índices + sequence + seed

 Sem credenciais sensíveis no repositório (env vars / -D / prompt)

 UTF-8 configurado e saída limpa ao encerrar

📄 Licença

Uso acadêmico/educacional.


---

se quiser, eu também te gero um **`README_EN.md`** em inglês para ficar ainda mais completo no GitHub — mas, para a avaliação, esse README já está redondinho.
::contentReference[oaicite:0]{index=0}
