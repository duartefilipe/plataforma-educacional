# Plataforma Educacional - Spring Boot

## 1. Visão Geral

Este projeto implementa uma plataforma web educacional utilizando Spring Boot e Java 17. O sistema permite o gerenciamento de escolas, professores e alunos, oferecendo funcionalidades como:

*   Autenticação e autorização baseada em papéis (ADMIN, PROFESSOR, ALUNO).
*   Cadastro (CRUD) de Escolas, Professores e Alunos pelo Administrador.
*   Associação muitos-para-muitos entre Professores e Escolas.
*   Associação muitos-para-muitos entre Alunos e Escolas (considerando turnos e ano letivo).
*   Área de Planejamento para Professores: Criação, upload e gerenciamento de atividades (texto ou arquivos).
*   Área de Compartilhamento: Professores podem compartilhar suas atividades, categorizá-las e pesquisar/filtrar atividades compartilhadas por outros.
*   Área do Aluno: Visualização de atividades designadas por professores e submissão de respostas (texto ou arquivo).

## 2. Pré-requisitos

Para construir e executar este projeto localmente, você precisará de:

*   **Java Development Kit (JDK) 17 ou superior:** Certifique-se de que a variável de ambiente `JAVA_HOME` esteja configurada corretamente.
*   **Apache Maven:** Utilizado para gerenciamento de dependências e build do projeto. O projeto inclui o Maven Wrapper (`mvnw`), que pode baixar o Maven automaticamente se necessário.
*   **PostgreSQL:** Banco de dados relacional utilizado para persistência. Crie um banco de dados (ex: `plataforma_educacional`).

## 3. Configuração

1.  **Banco de Dados:**
    *   Abra o arquivo `src/main/resources/application.properties`.
    *   O repositório já traz algumas configurações de exemplo:
        ```properties
        spring.datasource.url=jdbc:postgresql://192.168.100.109:15432/plataformaeducacional
        spring.datasource.username=postgres
        spring.datasource.password=Eunaoseiasenha22
        ```
    *   Ajuste a URL e as credenciais de acordo com o seu ambiente local.
    *   A propriedade `spring.jpa.hibernate.ddl-auto=update` tentará criar/atualizar o schema do banco de dados automaticamente na inicialização. Para ambientes de produção, considere usar `validate` ou `none` e gerenciar o schema com ferramentas como Flyway ou Liquibase.

2.  **Usuário Administrador Inicial:**
    *   A classe `DataLoader` (`src/main/java/br/com/plataformaeducacional/config/DataLoader.java`) cria um usuário administrador inicial na primeira execução se não houver nenhum usuário no banco.
    *   **Email:** `admin@plataforma.com`
    *   **Senha:** `admin123`
    *   É altamente recomendável alterar esta senha após o primeiro login.

3.  **Armazenamento de Arquivos:**
    *   Os arquivos enviados (atividades dos professores, respostas dos alunos) são armazenados localmente no diretório `/home/ubuntu/plataforma-uploads/` (subdividido em `atividades` e `respostas-alunos`).
    *   **Importante:** Este caminho está configurado nos arquivos `AtividadeServiceImpl.java` e `AlunoAtividadeServiceImpl.java`. Em um ambiente de produção, este caminho deve ser ajustado para um local apropriado no servidor ou, preferencialmente, substituído por uma solução de armazenamento em nuvem (como AWS S3, Google Cloud Storage, etc.). Certifique-se de que a aplicação tenha permissões de escrita no diretório escolhido.

## 4. Construindo e Executando

1.  **Navegue até o diretório raiz do projeto:**
    ```bash
    cd /home/ubuntu/plataforma-educacional
    ```
2.  **Compile o projeto:**
    ```bash
    ./mvnw clean compile
    ```
3.  **Execute a aplicação:**
    ```bash
    ./mvnw spring-boot:run
    ```
    A aplicação estará disponível, por padrão, em `http://localhost:8081`.

Alternativamente, você pode gerar um arquivo JAR executável:

1.  **Empacote a aplicação:**
    ```bash
    ./mvnw clean package
    ```
2.  **Execute o JAR:**
    ```bash
    java -jar target/plataforma-educacional-0.0.1-SNAPSHOT.jar
    ```

## 5. Estrutura da API

O sistema expõe uma API RESTful. A autenticação é gerenciada pelo Spring Security (usando login baseado em formulário por padrão). Os principais endpoints são:

*   **Autenticação:** Endpoints padrão do Spring Security (`/login`, `/logout`).
*   **Escolas:** `/api/escolas/**` (CRUD - Requer ADMIN)
*   **Professores:** `/api/professores/**` (CRUD - Requer ADMIN)
*   **Alunos:** `/api/alunos/**` (CRUD - Requer ADMIN)
*   **Atividades (Planejamento Professor):** `/api/atividades/**` (CRUD, Upload, Download - Requer PROFESSOR)
*   **Atividades Compartilhadas:** `/api/atividades/compartilhadas/**` (Compartilhar, Remover Compartilhamento, Buscar com Filtros - Requer PROFESSOR para compartilhar/remover, qualquer usuário autenticado para buscar)
*   **Atividades (Área Aluno):** `/api/aluno/atividades/**` (Listar Designadas, Ver Detalhes, Marcar Visualizada, Submeter Resposta - Requer ALUNO)

Consulte a classe `SecurityConfig.java` para detalhes sobre as permissões de cada endpoint.

## 6. Estrutura do Banco de Dados

As entidades do sistema estão mapeadas via JPA no pacote `br.com.plataformaeducacional.model`. Consulte o código fonte para detalhes de tabelas, colunas e relacionamentos.

## 7. Limitações Conhecidas (Ambiente de Desenvolvimento/Sandbox)

*   Durante o desenvolvimento neste ambiente, foi identificado que a variável de ambiente `JAVA_HOME` não estava configurada, impedindo a compilação e execução de testes automatizados via Maven Wrapper (`./mvnw`). Certifique-se de que o Java 17 esteja corretamente instalado e configurado no ambiente onde a aplicação será executada.
*   A implementação atual utiliza armazenamento local de arquivos. Para produção, é essencial migrar para uma solução de armazenamento mais robusta e escalável.
*   A segurança (CSRF, HTTPS, tratamento de senhas, etc.) foi implementada de forma básica. Recomenda-se uma revisão e aprimoramento da segurança para ambientes de produção.

---

Este README fornece uma visão geral e instruções básicas. Para detalhes específicos sobre a implementação, consulte o código-fonte e os comentários nas classes relevantes.

## 8. Docker e Frontend

Um diretório `frontend/` com um esqueleto de aplicação React foi incluído neste repositório. O backend e o frontend possuem `Dockerfile` dedicados e um `docker-compose.yml` simplifica a execução de ambos os serviços juntamente com o banco PostgreSQL.

Para iniciar tudo com Docker, copie o arquivo `.env.example` para `.env` e execute:

```bash
cp .env.example .env
docker compose up --build
```

O arquivo `.env` define variáveis como `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, além de chaves opcionais (`OPENAI_API_KEY`, `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD`). Ajuste-as conforme o seu ambiente.

O frontend estará disponível em `http://localhost:3000` e o backend em `http://localhost:8081`.

