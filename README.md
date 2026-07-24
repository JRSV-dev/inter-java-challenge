# Inter Java Challenge — API de Remessas

API REST desenvolvida para o desafio técnico, com foco no cadastro de usuários e na evolução de operações de remessa entre carteiras em Real e Dólar.

O projeto utiliza uma abordagem **API First**, com o contrato da aplicação definido em OpenAPI, persistência com MyBatis, conversão de objetos com MapStruct e validações desacopladas por responsabilidade.

---

## Tecnologias

- Java 21
- Spring Boot
- Spring Web
- Spring Validation
- Spring Transaction
- Spring Security Crypto / `PasswordEncoder`
- MyBatis
- MapStruct
- Lombok
- OpenAPI 3
- Swagger UI
- Banco de dados relacional em memória `H2`
- Maven
- JUnit 5
-  Mockito
-  AssertJ
- Spring Boot Test

---

## Funcionalidades

### Usuários

- Cadastro de usuário;
- Consulta de usuário por ID;
- Listagem paginada de usuários;
- Validação de e-mail único;
- Validação de CPF ou CNPJ único;
- Criptografia da senha antes da persistência;
- Tratamento de usuário não encontrado;
- Retorno de página vazia quando não existem registros;
- Persistência transacional durante o cadastro.

### Regras de negócio do desafio

- Usuários podem ser Pessoa Física ou Pessoa Jurídica;
- Pessoa Física utiliza CPF;
- Pessoa Jurídica utiliza CNPJ;
- E-mail, CPF e CNPJ devem ser únicos;
- Cada usuário possui uma carteira com saldo em Real e Dólar;
- O saldo deve ser validado antes de uma remessa;
- Pessoa Física possui limite diário de R$ 10.000,00;
- Pessoa Jurídica possui limite diário de R$ 50.000,00;
- São permitidas remessas entre PF e PJ em qualquer direção;
- Aos finais de semana deve ser utilizada a última cotação disponível;
- Toda transferência deve ocorrer dentro de uma transação;
- Em caso de inconsistência, a operação deve ser revertida integralmente.

---

## Arquitetura

O projeto separa as responsabilidades entre as seguintes camadas:

```text
Controller gerado pelo OpenAPI
        ↓
Serviços
        ↓
Validadores / Business auxiliares
        ↓
Repository
        ↓
MyBatis Mapper XML
        ↓
Banco de dados
```

### Business

A classe `UsuarioBusiness` concentra os casos de uso relacionados ao usuário:

- `buscarUsuarioPorId`;
- `buscarUsuarios`;
- `salvarNovoUsuario`.

O cadastro utiliza `@Transactional`, garantindo rollback caso alguma etapa da operação falhe.

### Repository

A interface `UsuarioRepository` define as operações de persistência:

```java
Optional<PaginaUsuario> buscarTodosUsuarios(
        Integer quantidadePorPagina,
        Integer pagina
);

Optional<Usuario> buscarUsuarioPorId(Long usuarioId);

Optional<Usuario> buscarUsuarioPorIdentificador(String identificador);

Optional<Usuario> buscarUsuarioPorEmail(String email);

Long salvarNovoUsuario(Usuario usuario);
```

As consultas SQL ficam separadas em:

```text
src/main/resources/mappers/UsuarioMapper.xml
```

### MapStruct

O `UsuarioMapper` é responsável pela conversão entre os modelos da API e os modelos internos:

```java
UsuarioResponse modelParaResponse(Usuario usuario);

Usuario requestParaModel(UsuarioRequest request);

List<UsuarioResponse> modelParaResponse(List<Usuario> usuarios);
```

O campo `id` é ignorado no mapeamento de entrada porque é gerado pelo banco de dados.

```java
@Mapping(target = "id", ignore = true)
Usuario requestParaModel(UsuarioRequest request);
```

### Validações

As validações seguem uma estratégia extensível baseada na interface genérica:

```java
public interface Validador<T> {

    void validar(T objeto);
}
```

Cada regra possui uma implementação independente, por exemplo:

```java
public class EmailUsuarioUnicoValidador
        implements Validador<UsuarioRequest> {
}
```

O `CriacaoUsuarioValidador` recebe todos os validadores registrados pelo Spring e executa cada um deles:

```java
private final List<Validador<UsuarioRequest>> validadores;

public void validar(UsuarioRequest request) {
    validadores.forEach(validador -> validador.validar(request));
}
```

A anotação `@Order` pode ser utilizada para controlar a ordem de execução.

### Busca centralizada

A classe `BuscarUsuario` centraliza a consulta por ID e o lançamento da exceção de usuário não encontrado:

```java
public Usuario buscarPorId(Long usuarioId) {
    return usuarioRepository.buscarUsuarioPorId(usuarioId)
            .orElseThrow(UsuarioNaoEncontradoException::new);
}
```

Isso evita duplicação da mesma regra em diferentes casos de uso.

---

## Estrutura do projeto

```text
src
└── main
    ├── java
    │   └── com.inter.java.challenge
    │       ├── api
    │       ├── business
    │       ├── configuration
    │       │   └── exception
    │       ├── factory
    │       ├── mapper
    │       ├── model
    │       ├── repository
    │       ├── service
    │       └── validator
    └── resources
        ├── db
        │   └── schema.sql
        ├── mappers
        │   └── UsuarioMapper.xml
        ├── openapi
        │   └── openapi.yaml
        └── application.properties
```

---

## Banco de dados

O script de criação das tabelas está em:

```text
src/main/resources/db/schema.sql
```

Exemplo da tabela de usuários:

```sql
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    nome_completo VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    identificador VARCHAR(14) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL
);
```

O campo `identificador` armazena CPF ou CNPJ.

A unicidade também deve ser garantida no banco de dados, mesmo existindo validação na aplicação. Dessa forma, a API permanece protegida contra condições de corrida durante cadastros simultâneos.

---

## Documentação da API

O contrato OpenAPI está localizado em:

```text
src/main/resources/openapi/openapi.yaml
```

Após iniciar a aplicação, a documentação pode ser acessada em:

```text
http://localhost:8080/swagger-ui/index.html
```

Contrato OpenAPI em formato JSON:

```text
http://localhost:8080/v3/api-docs
```

> As rotas definitivas devem seguir o contrato definido no arquivo `openapi.yaml`.

---

## Endpoints de usuários

### Cadastrar usuário

```http
POST /usuarios
```

#### Requisição

```json
{
  "nomeCompleto": "João da Silva",
  "email": "joao.silva@email.com",
  "identificador": "12345678901",
  "senha": "Senha@123"
}
```

#### Resposta

```json
{
  "id": 1,
  "nomeCompleto": "João da Silva",
  "email": "joao.silva@email.com",
  "identificador": "12345678901"
}
```

Possíveis respostas:

- `201 Created`: usuário cadastrado;
- `400 Bad Request`: dados inválidos;
- `409 Conflict`: e-mail ou identificador já cadastrado;
- `500 Internal Server Error`: erro inesperado.

---

### Buscar usuário por ID

```http
GET /usuarios/{usuarioId}
```

Exemplo:

```http
GET /usuarios/1
```

Possíveis respostas:

- `200 OK`: usuário encontrado;
- `404 Not Found`: usuário não encontrado.

---

### Listar usuários

```http
GET /usuarios?pagina=0&quantidadePorPagina=10
```

Parâmetros:

| Parâmetro | Descrição |
|---|---|
| `pagina` | Número da página solicitada |
| `quantidadePorPagina` | Quantidade máxima de registros por página |

Exemplo de resposta:

```json
{
  "pagina": 0,
  "quantidadePagina": 10,
  "totalElementos": 1,
  "totalPaginas": 1,
  "conteudo": [
    {
      "id": 1,
      "nomeCompleto": "João da Silva",
      "email": "joao.silva@email.com",
      "identificador": "12345678901"
    }
  ]
}
```

---

## Mensagens de erro

As mensagens são centralizadas no enum `MensagensExceptions`.

Exemplos:

```text
Usuário não encontrado.
Usuário já cadastrado com esse e-mail.
Usuário já cadastrado com esse identificador.
```

As exceções de domínio devem ser tratadas por um handler global, mantendo o mesmo formato de resposta em toda a API.

---

## Executando o projeto

### Pré-requisitos

- Java 21;
- Maven 3.9 ou superior.

### Clonar o repositório

```bash
git clone <URL_DO_REPOSITORIO>
cd inter-java-challenge
```

### Compilar

Linux ou macOS:

```bash
./mvnw clean install
```

Windows:

```bash
mvnw.cmd clean install
```

### Executar

Linux ou macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```bash
mvnw.cmd spring-boot:run
```

A aplicação será iniciada, por padrão, em:

```text
http://localhost:8080
```

---

## Executando os testes

Linux ou macOS:

```bash
./mvnw test
```

Windows:

```bash
mvnw.cmd test
```

---

## Decisões técnicas

### API First

O contrato é definido antes da implementação no arquivo `openapi.yaml`. A geração dos modelos e interfaces reduz divergências entre documentação e código.

### Validações desacopladas

Cada regra de validação é implementada separadamente. Novas validações podem ser adicionadas sem alterar o fluxo principal de cadastro.

### Consulta centralizada

A classe `BuscarUsuario` concentra a regra de busca e tratamento de ausência, reduzindo duplicação.

### Transações

Operações que alteram dados utilizam `@Transactional`. Em uma remessa, débito, conversão e crédito devem pertencer à mesma transação.

### Segurança da senha

A senha é criptografada com `PasswordEncoder` antes de ser enviada ao repositório:

```java
model.setSenha(passwordEncoder.encode(usuarioRequest.getSenha()));
```

A senha não deve ser retornada nas respostas da API.

### Persistência com MyBatis

O MyBatis mantém as consultas SQL explícitas e separadas da regra de negócio, facilitando controle, análise e otimização.

---

## Testes unitários

Os testes unitários da aplicação foram implementados com o objetivo de validar as regras de negócio de forma isolada, sem acessar banco de dados, servidor HTTP ou outros componentes externos.

A classe principal testada é a `UsuarioBusiness`, responsável por coordenar os fluxos relacionados aos usuários e às suas carteiras.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### Organização dos testes

Os testes seguem o padrão AAA:

* **Arrange:** preparação dos dados e configuração dos mocks.
* **Act:** execução do método que está sendo testado.
* **Assert:** validação do resultado e das interações realizadas.

Exemplo:

```java
@Test
void deveBuscarUsuarioPorIdComSucesso() {
    // Arrange
    Long usuarioId = 1L;
    Usuario usuario = UsuarioFixture.criarUsuarioSalvo();
    UsuarioResponse response = UsuarioFixture.criarUsuarioResponse();

    when(buscarUsuario.buscarPorId(usuarioId))
            .thenReturn(usuario);

    when(usuarioMapper.modelParaResponse(usuario))
            .thenReturn(response);

    // Act
    UsuarioResponse resultado =
            usuarioBusiness.buscarUsuarioPorId(usuarioId);

    // Assert
    assertThat(resultado).isEqualTo(response);

    verify(buscarUsuario).buscarPorId(usuarioId);
    verify(usuarioMapper).modelParaResponse(usuario);
}
```

### Dependências simuladas

As dependências da `UsuarioBusiness` são simuladas com Mockito, garantindo que somente o comportamento da classe de negócio seja testado.

```java
@ExtendWith(MockitoExtension.class)
class UsuarioBusinessTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PaginaUsuarioVaziaFactory paginaUsuarioFactory;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private CriacaoUsuarioValidador criacaoUsuarioValidador;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private BuscarUsuario buscarUsuario;

    @Mock
    private CarteiraFactory carteiraFactory;

    @Mock
    private CarteiraRepository carteiraRepository;

    @Mock
    private CarteiraMapper carteiraMapper;

    @Mock
    private BuscarCarteira buscarCarteira;

    @InjectMocks
    private UsuarioBusiness usuarioBusiness;
}
```

### Massa de dados

Os objetos utilizados nos testes são criados por classes de fixture. Isso reduz a duplicação de código e mantém os dados de teste centralizados e reutilizáveis.

```java
public final class UsuarioFixture {

    public static final Long USUARIO_ID = 1L;
    public static final Long CARTEIRA_ID = 10L;

    private UsuarioFixture() {
    }

    public static UsuarioRequest criarRequestPf() {
        return new UsuarioRequest()
                .nomeCompleto("João da Silva")
                .email("joao@email.com")
                .identificador("12345678901")
                .senha("Senha@123");
    }

    public static Usuario criarUsuarioSalvo() {
        return Usuario.builder()
                .id(USUARIO_ID)
                .nomeCompleto("João da Silva")
                .email("joao@email.com")
                .identificador("12345678901")
                .tipoUsuario(TipoUsuario.PF)
                .senha("senha-criptografada")
                .build();
    }

    public static UsuarioResponse criarUsuarioResponse() {
        return new UsuarioResponse()
                .id(USUARIO_ID)
                .nomeCompleto("João da Silva")
                .email("joao@email.com")
                .identificador("12345678901");
    }
}
```

### Cenários testados

#### Cadastro de usuário

O fluxo de cadastro valida:

* execução das validações antes da persistência;
* conversão do request para o model de domínio;
* criptografia da senha;
* persistência do novo usuário;
* criação automática da carteira;
* persistência da carteira com saldos iniciais;
* conversão do usuário salvo para o response da API;
* interrupção do fluxo quando a validação falha;
* propagação de erros durante a criação da carteira.

#### Consulta de usuário por ID

O fluxo de consulta individual valida:

* busca do usuário pelo identificador;
* conversão do model para `UsuarioResponse`;
* retorno correto dos dados do usuário;
* propagação da exceção quando o usuário não é encontrado.

#### Consulta paginada de usuários

O fluxo de paginação valida:

* consulta dos usuários com os parâmetros de página e quantidade;
* conversão de `Pagina<Usuario>` para `PaginaUsuario`;
* retorno do conteúdo e dos metadados de paginação;
* retorno de uma página vazia quando nenhum usuário é encontrado.

#### Consulta da carteira

O fluxo de consulta da carteira valida:

* busca da carteira pelo ID do usuário;
* conversão de `Carteira` para `CarteiraResponse`;
* retorno dos saldos em real e dólar;
* propagação da exceção quando a carteira não é encontrada.

### Validação das interações

Além dos valores retornados, os testes verificam se as dependências foram chamadas corretamente.

```java
verify(criacaoUsuarioValidador).validar(request);
verify(usuarioRepository).salvarNovoUsuario(any(Usuario.class));
verify(carteiraFactory).criarParaUsuario(USUARIO_ID);
verify(carteiraRepository).salvarNovaCarteira(carteira);
```

O `ArgumentCaptor` é utilizado para verificar os dados enviados ao repository:

```java
ArgumentCaptor<Usuario> captor =
        ArgumentCaptor.forClass(Usuario.class);

verify(usuarioRepository)
        .salvarNovoUsuario(captor.capture());

Usuario usuarioEnviado = captor.getValue();

assertThat(usuarioEnviado.getSenha())
        .isEqualTo("senha-criptografada");
```

### Execução dos testes

Para executar todos os testes:

```bash
mvn test
```

Para executar somente os testes da `UsuarioBusiness`:

```bash
mvn -Dtest=UsuarioBusinessTest test
```

Para executar um cenário específico:

```bash
mvn -Dtest=UsuarioBusinessTest#deveSalvarUsuarioECriarCarteira test
```

## Autor

Desenvolvido por **Jhônata Ruan**