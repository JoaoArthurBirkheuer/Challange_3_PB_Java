# Ecommerce Spring Boot API

### Visão Geral

Este projeto é uma aplicação de e-commerce construída com Spring Boot, projetada para gerenciar produtos, controlar o estoque, processar pedidos e fornecer funcionalidades de autenticação e autorização robustas. A API é dividida em camadas lógicas para garantir a manutenibilidade e escalabilidade, seguindo os princípios de uma arquitetura RESTful.

### Arquitetura e Estrutura do Projeto

O projeto é organizado em várias camadas, cada uma com responsabilidades bem definidas, para promover a separação de preocupações e facilitar o desenvolvimento e a manutenção.

#### Estrutura de Pacotes

* `br.com.compass.challenge3SpringBoot`
    * `controller`: Contém os controladores REST que lidam com as requisições HTTP de entrada, delegando a lógica de negócios para a camada de serviço.
    * `service`: Responsável pela lógica de negócios da aplicação, orquestrando as operações e interagindo com a camada de repositório.
    * `repository`: Interfaces que estendem `JpaRepository` para acesso a dados, definindo métodos para operações CRUD e consultas personalizadas.
    * `entity`: Classes de entidade que representam as tabelas no banco de dados.
    * `dto`: Objetos de Transferência de Dados (DTOs) usados para entrada e saída de dados das APIs, garantindo que apenas os dados necessários sejam expostos.
    * `exception`: Classes de exceção personalizadas para lidar com erros específicos da aplicação de forma padronizada.
    * `mapper`: Classes responsáveis por mapear entre entidades e DTOs.
    * `security`: Contém classes relacionadas à segurança, como filtros JWT e utilitários de token.
    * `config`: Classes de configuração para a aplicação, incluindo segurança e inicialização de dados.
 
### Entidades

* **`Usuario`**: Representa um usuário do sistema, que pode ser um `ROLE_ADMIN` ou um `ROLE_CLIENTE`. Contém informações de autenticação (email, senha), dados pessoais (nome) e gerencia relacionamentos com `Carrinho`, `Pedido` e `PasswordResetToken`. Possui um status de exclusão lógica (`deleted`).
* **`Carrinho`**: Representa o carrinho de compras de um `Usuario`. Cada usuário tem um único carrinho, que armazena os `ItemCarrinho`s.
* **`ItemCarrinho`**: Representa um item específico dentro de um `Carrinho`, associando um `Produto` a uma determinada `quantidade`.
* **`Pedido`**: Representa uma compra realizada por um `Usuario` (`cliente`). Contém a data da compra, o `StatusPedido` atual, o `total` da compra e uma lista de `ItemPedido`s.
* **`ItemPedido`**: Representa um item específico dentro de um `Pedido`, registrando o `Produto`, a `quantidade` comprada e o `precoUnitario` do produto no momento da compra.
* **`Produto`**: Representa um item disponível para venda no e-commerce. Contém informações como `nome`, `descricao`, `preco`, `estoque` e um status de `ativo`. Também possui um status de exclusão lógica para quando não pode ser mais vendido.
* **`PasswordResetToken`**: Entidade usada para gerenciar o processo de redefinição de senha. Armazena um token único (`UUID`) que expira em um determinado `expiresAt` e está associado a um `Usuario`.
* **`Role`**: Um `enum` que define os diferentes papéis de usuário no sistema: `ROLE_ADMIN` e `ROLE_CLIENTE`.
* **`StatusPedido`**: Um `enum` que define os possíveis estados de um `Pedido`: `PENDENTE`, `PROCESSANDO`, `ENVIADO`, `ENTREGUE` e `CANCELADO`.
* **`BaseEntity`**: Uma classe base abstrata que fornece campos de auditoria comuns (`createdAt`, `updatedAt`, `deleted`, `version`) para as outras entidades do projeto, garantindo consistência e rastreabilidade.

### Diagrama de Classes

![image](https://github.com/user-attachments/assets/41042852-25ec-45e0-bd55-8d87de4fbf35)

### Serviços

A camada de serviço encapsula a lógica de negócios da aplicação, orquestrando as operações entre os repositórios e DTOs, e aplicando as regras de negócio.

* **`AuthenticationService`**: Gerencia as operações de autenticação e registro de usuários.
    * Realiza o login de usuários e gera tokens JWT.
    * Cadastra novos usuários (clientes e administradores), verificando a unicidade do email.
    * Gera e envia tokens para redefinição de senha via e-mail.
    * Atualiza senhas utilizando tokens de redefinição.
* **`CartService`**: Responsável pela lógica de manipulação do carrinho de compras.
    * Adiciona itens ao carrinho, verificando estoque e criando um novo carrinho se necessário.
    * Remove itens do carrinho (soft delete).
    * Limpa todos os itens de um carrinho.
    * Visualiza o conteúdo do carrinho, calculando o valor total.
    * Finaliza a compra, convertendo o carrinho em um pedido e atualizando o estoque dos produtos.
* **`CustomUserDetailsService`**: Implementação do `UserDetailsService` do Spring Security.
    * Carrega os detalhes do usuário pelo email para o processo de autenticação.
* **`EmailService`**: Serviço para envio de e-mails, como os de redefinição de senha.
    * Envia e-mails de forma assíncrona para não bloquear a thread principal da aplicação.
* **`OrderService`**: Gerencia as operações relacionadas aos pedidos.
    * Lista todos os pedidos ou apenas os pedidos de um usuário específico, com paginação.
    * Busca detalhes de um pedido por ID, com validação de acesso.
    * Atualiza o status de um pedido, ajustando o estoque dos produtos se o status for `CANCELADO`.
* **`ProductService`**: Contém a lógica de negócios para a gestão de produtos.
    * Cria, atualiza e busca produtos.
    * Lista produtos com filtros por nome, status de atividade e exclusão lógica.
    * Realiza a exclusão lógica (`deletar`) de produtos, com validação para impedir a exclusão se o produto estiver em pedidos ativos.
    * Inativa e reativa produtos.
* **`ReportService`**: Responsável pela geração dos relatórios administrativos.
    * Gera relatórios de vendas por período, incluindo total de vendas, receita, lucro e produtos mais vendidos.
    * Lista produtos com baixo estoque.
    * Lista os produtos mais vendidos.
    * Identifica os principais clientes por número de pedidos e por total gasto.
* **`UserService`**: Gerencia as operações de usuários, além das tratadas em `AuthenticationService`.
    * Lista usuários com paginação, incluindo a opção de listar usuários deletados (apenas para ADMIN).
    * Busca usuários por ID e email.
    * Atualiza dados do perfil do usuário, com validação de permissão e unicidade de email.
    * Deleta (soft delete) usuários, com validação para impedir a exclusão se o usuário tiver pedidos em andamento.

### Funcionalidades

A API oferece as seguintes funcionalidades principais:

* **Autenticação e Autorização via JWT**:
    * Registro de novos usuários (clientes e administradores).
    * Login de usuários com geração de JWT para acesso a recursos protegidos.
    * Controle de acesso baseado em perfis (`ROLE_ADMIN` e `ROLE_CLIENTE`).
    * Redefinição de senha com envio de token por e-mail.
* **Gestão de Produtos**:
    * Criação, leitura, atualização e exclusão de produtos (somente administradores).
    * Inativação de produtos que já foram inclusos em vendas.
    * Controle de estoque para prevenir vendas de produtos com estoque insuficiente.
    * Listagem de produtos com filtros por nome e opções para incluir inativos/deletados (administradores).
* **Carrinho de Compras**:
    * Adicionar e remover itens do carrinho.
    * Limpar todo o carrinho de compras.
    * Visualizar o conteúdo do carrinho com detalhes dos itens e valor total.
    * Finalizar a compra, criando um pedido e atualizando o estoque.
* **Gestão de Pedidos**:
    * Listagem de todos os pedidos (administradores).
    * Listagem dos pedidos do cliente autenticado.
    * Detalhes de um pedido específico.
    * Atualização do status do pedido (somente administradores).
* **Relatórios Administrativos**:
    * **Relatório de Vendas**: Total de vendas, receita e lucro em um período específico, com os produtos mais vendidos.
    * **Produtos com Baixo Estoque**: Lista de produtos abaixo de um limite de estoque definido.
    * **Produtos Mais Vendidos**: Ranking dos produtos mais vendidos.
    * **Clientes Top por Pedidos**: Clientes que realizaram mais pedidos.
    * **Clientes Top por Gastos**: Clientes que mais gastaram.

### Tecnologias Utilizadas

* **Java 21**: Linguagem de programação.
* **Spring Boot 3.4.5**: Framework para construção de aplicações Java.
    * `spring-boot-starter-web`: Para a construção de APIs RESTful.
    * `spring-boot-starter-data-jpa`: Para persistência de dados com JPA.
    * `spring-boot-starter-security`: Para autenticação e autorização baseada em Spring Security.
    * `spring-boot-starter-mail`: Para envio de e-mails.
    * `spring-boot-starter-validation`: Para validação de DTOs.
    * `spring-boot-devtools`: Ferramentas para desenvolvimento (hot-reloading, etc.).
* **MySQL Connector/J**: Driver JDBC para conexão com o banco de dados MySQL.
* **Lombok**: Biblioteca para reduzir código boilerplate.
* **jjwt (JSON Web Token)**: Para implementação de tokens JWT.
* **ModelMapper**: Para mapeamento de objetos entre camadas.

### Endpoints da API

A seguir, uma lista dos principais endpoints da API, categorizados por funcionalidade e perfil de acesso:

#### Autenticação (`/auth`)

* `POST /auth/login`: Autentica um usuário e retorna um JWT.
    * **Requisição**: `LoginRequestDTO` (email, senha)
    * **Resposta**: `LoginResponseDTO` (token JWT)
    * **Acesso**: Público
* `POST /auth/register`: Registra um novo usuário cliente.
    * **Requisição**: `RegisterRequestDTO` (email, nome, senha)
    * **Resposta**: `RegisterResponseDTO` (mensagem de sucesso)
    * **Acesso**: Público
* `POST /auth/update-password-request`: Solicita um token de redefinição de senha para o e-mail informado.
    * **Requisição**: `PasswordResetRequestDTO` (email)
    * **Resposta**: `RegisterResponseDTO` (mensagem de sucesso)
    * **Acesso**: Público
* `POST /auth/update-password`: Atualiza a senha do usuário usando o token de redefinição.
    * **Requisição**: `PasswordUpdateDTO` (token, senhaAtual, novaSenha)
    * **Resposta**: `PasswordUpdateResponseDTO` (mensagem de sucesso)
    * **Acesso**: Público

#### Administração (`/admin`)

* `POST /admin/register`: Registra um novo usuário administrador.
    * **Requisição**: `RegisterRequestDTO` (email, nome, senha)
    * **Resposta**: `RegisterResponseDTO` (mensagem de sucesso)
    * **Acesso**: `ROLE_ADMIN`

#### Produtos (`/products`)

* `POST /products`: Cria um novo produto.
    * **Requisição**: `ProductRequestDTO`
    * **Resposta**: `ProductResponseDTO`
    * **Acesso**: `ROLE_ADMIN`
* `GET /products`: Lista produtos com paginação e filtros.
    * **Parâmetros**: `nome` (opcional), `includeInactive` (boolean, default: false), `includeDeleted` (boolean, default: false), `page`, `size`
    * **Resposta**: `PageResponseDTO<ProductResponseDTO>`
    * **Acesso**: `ROLE_ADMIN`, `ROLE_CLIENTE` (`includeInactive` e `includeDeleted` restritos a `ROLE_ADMIN`)
* `GET /products/{id}`: Busca um produto por ID.
    * **Resposta**: `ProductResponseDTO`
    * **Acesso**: `ROLE_ADMIN`, `ROLE_CLIENTE`
* `PUT /products/{id}`: Atualiza um produto existente.
    * **Requisição**: `ProductRequestDTO`
    * **Resposta**: `ProductResponseDTO`
    * **Acesso**: `ROLE_ADMIN`
* `DELETE /products/{id}`: Deleta (soft delete) um produto.
    * **Acesso**: `ROLE_ADMIN`
* `PATCH /products/{id}/inactivate`: Inativa um produto.
    * **Acesso**: `ROLE_ADMIN`
* `PATCH /products/{id}/reactivate`: Reativa um produto.
    * **Acesso**: `ROLE_ADMIN`

#### Carrinho de Compras (`/cart`)

* `POST /cart/items`: Adiciona um item ao carrinho do usuário autenticado.
    * **Requisição**: `CartItemRequestDTO` (productId, quantity)
    * **Resposta**: `MessageResponseDTO`
    * **Acesso**: `ROLE_CLIENTE`
* `DELETE /cart/items/{itemId}`: Remove um item do carrinho do usuário autenticado.
    * **Acesso**: `ROLE_CLIENTE`
* `DELETE /cart/clear`: Limpa o carrinho do usuário autenticado.
    * **Resposta**: `MessageResponseDTO`
    * **Acesso**: `ROLE_CLIENTE`
* `GET /cart`: Visualiza o carrinho do usuário autenticado.
    * **Resposta**: `CartResponseDTO`
    * **Acesso**: `ROLE_CLIENTE`
* `POST /cart/checkout`: Finaliza a compra do carrinho atual.
    * **Resposta**: `MessageResponseDTO`
    * **Acesso**: `ROLE_CLIENTE`

#### Pedidos (`/orders`)

* `GET /orders`: Lista todos os pedidos (ADMIN) ou os pedidos do cliente autenticado (CLIENTE).
    * **Parâmetros**: `page`, `size`
    * **Resposta**: `PageResponseDTO<OrderSummaryDTO>`
    * **Acesso**: `ROLE_ADMIN`, `ROLE_CLIENTE`
* `GET /orders/{orderId}`: Busca detalhes de um pedido específico.
    * **Resposta**: `OrderDetailDTO`
    * **Acesso**: `ROLE_CLIENTE` (apenas seus próprios pedidos)
* `PUT /orders/{id}/status`: Atualiza o status de um pedido.
    * **Parâmetros**: `status` (ex: `PENDENTE`, `PROCESSANDO`, `ENVIADO`, `ENTREGUE`, `CANCELADO`)
    * **Acesso**: `ROLE_ADMIN`

#### Relatórios (`/reports`)

* `GET /reports/sales`: Gera um relatório de vendas para um período.
    * **Parâmetros**: `from` (yyyy-MM-dd), `to` (yyyy-MM-dd), `topN` (opcional, default: 5)
    * **Resposta**: `SalesReportDTO`
    * **Acesso**: `ROLE_ADMIN`
* `GET /reports/low-stock`: Lista produtos com estoque abaixo de um limite.
    * **Parâmetros**: `threshold`, `page`, `size`
    * **Resposta**: `PageResponseDTO<LowStockProductDTO>`
    * **Acesso**: `ROLE_ADMIN`
* `GET /reports/most-sold`: Lista os produtos mais vendidos com paginação.
    * **Parâmetros**: `page`, `size`
    * **Resposta**: `PageResponseDTO<MostSoldProductDTO>`
    * **Acesso**: `ROLE_ADMIN`
* `GET /reports/top-clients/orders`: Lista os clientes que mais fizeram pedidos com paginação.
    * **Parâmetros**: `page`, `size`
    * **Resposta**: `PageResponseDTO<TopClientDTO>`
    * **Acesso**: `ROLE_ADMIN`
* `GET /reports/top-clients/spending`: Lista os clientes que mais gastaram com paginação.
    * **Parâmetros**: `page`, `size`
    * **Resposta**: `PageResponseDTO<TopClientDTO>`
    * **Acesso**: `ROLE_ADMIN`

#### Usuários (`/users`)

* `GET /users`: Lista todos os usuários (somente ADMIN) ou usuários ativos (ADMIN).
    * **Parâmetros**: `incluirDeletados` (boolean, default: false), `page`, `size`
    * **Resposta**: `PageResponseDTO<UserResponseDTO>`
    * **Acesso**: `ROLE_ADMIN`
* `GET /users/{id}`: Busca um usuário por ID.
    * **Resposta**: `UserResponseDTO`
    * **Acesso**: `ROLE_ADMIN`
* `PUT /users/{id}`: Atualiza os dados de um usuário.
    * **Requisição**: `UserUpdateRequestDTO` (nome, email)
    * **Resposta**: `UserResponseDTO`
    * **Acesso**: `ROLE_ADMIN` ou `ROLE_CLIENTE` (apenas seu próprio perfil)
* `DELETE /users/{id}`: Deleta (soft delete) um usuário.
    * **Acesso**: `ROLE_ADMIN` ou `ROLE_CLIENTE` (apenas seu próprio perfil, se não tiver pedidos ativos)

A aplicação estará disponível em `http://localhost:8080`.

### Observações Adicionais

* **Validações**: As validações de entrada são realizadas utilizando anotações `jakarta.validation` nos DTOs, e as exceções são tratadas globalmente para retornar respostas padronizadas.
* **Tratamento de Exceções**: Todas as exceções são capturadas pelo `GlobalExceptionHandler` e retornam um `ErrorResponseDTO` com timestamp, status HTTP, mensagem de erro e path da requisição.
* **Paginação**: Métodos que retornam listas grandes de dados utilizam paginação para otimização de desempenho. As respostas de paginação são padronizadas via `PageResponseDTO`.
* **Auditoria**: Entidades estendem `BaseEntity` para incluir campos de auditoria `createdAt`, `updatedAt` e `deleted` automaticamente.
* **Inicialização do Admin**: Um usuário administrador padrão é criado na inicialização da aplicação, caso não exista, com o email `admin@exemplo.com` e senha `1234`.
