# Inter Java Challenge — API de Remessas

API REST para cadastro de usuários, consulta de carteiras e transferências
entre saldos em real e dólar.

O projeto segue uma abordagem API First: o contrato HTTP está definido em
OpenAPI e as interfaces da API são geradas durante o build. A aplicação usa
MyBatis para persistência, OpenFeign para consultar a PTAX do Banco Central e
H2 como banco de dados local.

## Funcionalidades

- Cadastro de usuários Pessoa Física e Pessoa Jurídica.
- Identificação automática do tipo de usuário pelo CPF ou CNPJ.
- Validação de e-mail e identificador únicos.
- Criptografia da senha antes da persistência.
- Criação automática de uma carteira para cada novo usuário.
- Consulta individual e listagem paginada de usuários.
- Consulta dos saldos em real e dólar.
- Transferências de real para dólar e de dólar para real.
- Validação de saldo e limite diário.
- Consulta da cotação do dólar no Banco Central.
- Uso da última cotação disponível em fins de semana e feriados.
- Tratamento padronizado dos erros de negócio.
- Testes unitários e de integração.

## Tecnologias

| Tecnologia | Finalidade |
|---|---|
| Java 21 | Linguagem da aplicação |
| Spring Boot 3.5 | Configuração e execução da API |
| Spring Web | Endpoints REST |
| Spring Validation | Validação das requisições |
| Spring Transaction | Controle transacional |
| Spring Cloud OpenFeign | Integração com o Banco Central |
| MyBatis | Persistência e consultas SQL |
| H2 | Banco em memória para desenvolvimento e testes |
| MapStruct | Conversão entre modelos internos e modelos da API |
| Lombok | Redução de código repetitivo |
| OpenAPI Generator | Geração das interfaces e modelos HTTP |
| Swagger UI | Visualização e execução dos endpoints |
| JUnit, Mockito e AssertJ | Testes unitários |
| Spring Boot Test e MockMvc | Testes de integração |
| WireMock | Simulação HTTP do Banco Central nos testes de integração |

## Organização do código

```text
src/main/java/com/inter/java/challenge
├── business/       coordenação dos casos de uso
├── client/         integração OpenFeign com o Banco Central
├── configuration/  beans e tratamento global de exceções
├── data/           enums, modelos e records internos
├── mapper/         conversões MapStruct
├── repository/     interfaces de persistência MyBatis
├── services/       implementação das interfaces geradas pelo OpenAPI
├── utils/          constantes e mensagens
└── workflows/      regras menores, validações, fábricas e movimentações
```

Recursos complementares:

```text
src/main/resources
├── dbo/schema.sql          criação das tabelas, constraints e índices
├── dbo/data.sql            dados demonstrativos do ambiente local
├── mappers/                comandos SQL do MyBatis
├── openapi/openapi.yaml    contrato HTTP da aplicação
└── application.properties configurações locais
```

### Responsabilidade das camadas

- `services`: recebe a chamada HTTP por meio das interfaces geradas pelo
  OpenAPI e delega para a camada de negócio.
- `business`: organiza o fluxo completo do caso de uso.
- `workflows`: concentra responsabilidades específicas, como buscar carteira,
  converter moeda, validar limite, debitar e creditar saldo.
- `repository`: define as operações de leitura e escrita.
- `mappers`: converte objetos da API em objetos internos e vice-versa.
- `client`: isola os detalhes da integração externa.

## Usuários

### Cadastro

Ao cadastrar um usuário, a aplicação:

1. normaliza o e-mail removendo espaços externos e convertendo-o para minúsculas;
2. valida os dígitos verificadores do CPF ou CNPJ;
3. valida se o e-mail já está cadastrado;
4. valida se o CPF ou CNPJ já está cadastrado;
5. identifica o tipo do usuário pelo identificador validado;
6. criptografa a senha;
7. salva o usuário;
8. cria uma carteira com os dois saldos zerados.

Regras do identificador:

| Identificador | Tipo |
|---|---|
| CPF com 11 dígitos e dígitos verificadores válidos | Pessoa Física (`PF`) |
| CNPJ com 14 dígitos e dígitos verificadores válidos | Pessoa Jurídica (`PJ`) |

Exemplo:

```http
POST /usuarios
Content-Type: application/json
```

```json
{
  "nomeCompleto": "Maria da Silva",
  "email": "maria@email.com",
  "identificador": "52998224725",
  "senha": "Senha@123"
}
```

### Listagem

```http
GET /usuarios?pagina=0&quantidadePorPagina=10
```

Parâmetros:

| Parâmetro | Padrão | Regra |
|---|---:|---|
| `pagina` | `0` | Deve ser maior ou igual a zero |
| `quantidadePorPagina` | `10` | Deve estar entre 1 e 100 |

### Consulta

```http
GET /usuarios/{id}
GET /usuarios/{id}/carteira
```

Uma carteira possui:

- `saldoReais`, armazenado com duas casas decimais;
- `saldoDolares`, armazenado com quatro casas decimais.

## Transferências

Endpoint:

```http
POST /transferencias
Content-Type: application/json
```

Requisição:

```json
{
  "usuarioOrigemId": 1,
  "usuarioDestinoId": 2,
  "moedaOrigem": "REAL",
  "valor": 100.00
}
```

Campos:

| Campo | Descrição |
|---|---|
| `usuarioOrigemId` | Usuário que terá o saldo debitado |
| `usuarioDestinoId` | Usuário que receberá o crédito |
| `moedaOrigem` | Moeda debitada: `REAL` ou `DOLAR` |
| `valor` | Valor positivo na moeda de origem |

### Conversão por moeda

#### Origem em real

Quando `moedaOrigem` é `REAL`:

1. o valor informado é debitado do saldo em reais da origem;
2. o valor é dividido pela cotação de compra;
3. o resultado convertido é creditado no saldo em dólares do destino.

Exemplo com cotação igual a `5.0000`:

```text
Débito:  R$ 100,00
Crédito: US$ 20,0000
```

#### Origem em dólar

Quando `moedaOrigem` é `DOLAR`:

1. o valor informado é debitado do saldo em dólares da origem;
2. o valor é multiplicado pela cotação de compra;
3. o resultado convertido é creditado no saldo em reais do destino.

Exemplo com cotação igual a `5.0000`:

```text
Débito:  US$ 20,0000
Crédito: R$ 100,00
```

### Fluxo interno

```text
Requisição
  → conversão para o modelo interno
  → consulta da última cotação disponível
  → cálculo dos valores em real e dólar
  → bloqueio das carteiras de origem e destino
  → consulta do total transferido no dia
  → validação de saldo e limite diário
  → débito da carteira de origem
  → crédito da carteira de destino
  → persistência do histórico
  → resposta com status CONCLUIDA
```

### Limites

Os limites são calculados pelo valor equivalente em real:

| Tipo de usuário | Limite diário |
|---|---:|
| Pessoa Física | R$ 10.000,00 |
| Pessoa Jurídica | R$ 50.000,00 |

O total já transferido pelo usuário no dia é somado ao valor da nova
operação. A transferência é rejeitada quando o total projetado ultrapassa o
limite.

### Saldo

A validação utiliza a moeda de origem:

- `REAL` consulta `saldoReal`;
- `DOLAR` consulta `saldoDolar`.

O débito SQL também verifica se ainda existe saldo suficiente no momento da
atualização.

### Concorrência e transação

As carteiras envolvidas são consultadas com `FOR UPDATE`. Isso mantém os
registros bloqueados durante a movimentação e reduz o risco de duas
transferências consumirem o mesmo saldo.

O método principal está anotado com `@Transactional`. Débito, crédito e
persistência do histórico participam da mesma transação. Se uma exceção de
negócio ou persistência interromper o fluxo, as alterações realizadas pela
transação são revertidas.

### Resposta

```json
{
  "id": 1,
  "usuarioOrigemId": 1,
  "usuarioDestinoId": 2,
  "valorReal": 100.00,
  "valorDolar": 20.0000,
  "cotacaoCompra": 5.0000,
  "dataCotacao": "2026-07-23",
  "dataTransferencia": "2026-07-24T10:30:00",
  "status": "CONCLUIDA"
}
```

O histórico registra os dois valores, a cotação utilizada, a data da cotação
e a data da transferência.

## Cotação do Banco Central

### Cliente Feign

`BancoCentralCotacaoFeignClient` declara a chamada HTTP para o serviço PTAX.
`BancoCentralBuscarCotacaoClient` atua como adaptador: prepara os parâmetros,
chama o Feign e converte a resposta externa em `CotacaoDolar`.

A URL fica fora do código:

```properties
integracoes.banco-central.base-url=https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata
```

### Última cotação disponível

O Banco Central não publica novas cotações aos finais de semana. Para atender
esse cenário, a consulta:

1. pesquisa uma janela retroativa de sete dias;
2. solicita apenas `cotacaoCompra` e `dataHoraCotacao`;
3. ordena por `dataHoraCotacao desc`;
4. limita o resultado a um registro.

Assim, uma transferência feita no sábado ou domingo utiliza normalmente a
cotação da sexta-feira. A mesma estratégia também cobre feriados dentro da
janela configurada.

```properties
integracoes.banco-central.dias-retroativos=7
```

Quando a resposta é nula ou não contém registros, o adaptador lança
`CotacaoIndisponivelException` e a API retorna `503 Service Unavailable`.

## Banco de dados

O schema contém três tabelas:

### `USUARIOS`

Armazena dados cadastrais, tipo, senha criptografada e datas de controle.
E-mail e identificador são únicos.

### `CARTEIRAS`

Possui uma relação de uma carteira por usuário e mantém os saldos separados.
O saldo em real usa duas casas decimais e o saldo em dólar utiliza quatro.
Constraints impedem saldos negativos. A coluna `data_atualizacao` registra a
última alteração de saldo.

### `TRANSFERENCIAS`

Registra:

- usuários de origem e destino;
- moeda de origem (`REAL` ou `DOLAR`);
- valor em real;
- valor em dólar;
- cotação utilizada;
- data da cotação;
- data da operação;
- status.

`VALOR_REAL` utiliza duas casas decimais. `VALOR_DOLAR` e `COTACAO_COMPRA`
utilizam quatro casas para que o histórico não perca a precisão usada na
conversão.

As constraints da tabela garantem:

- valores em real e dólar positivos;
- usuários de origem e destino diferentes;
- moeda de origem igual a `REAL` ou `DOLAR`;
- status válido igual a `CONCLUIDA`.

O índice composto por usuário de origem e data da transferência atende à
consulta usada no cálculo do limite diário.

O Spring executa os dois scripts durante a inicialização:

- `dbo/schema.sql`: cria tabelas, constraints e índice;
- `dbo/data.sql`: insere os usuários e carteiras usados na execução local.

Estrutura e dados ficam separados para facilitar a leitura e a manutenção.

## Tratamento de erros

As exceções de negócio estendem `ApiException` e são convertidas pelo
`GlobalExceptionHandler` para o seguinte formato:

```json
{
  "timestamp": "2026-07-24T10:30:00",
  "status": 422,
  "codigo": "SALDO_INSUFICIENTE",
  "mensagem": "Saldo insuficiente para realizar a transferência.",
  "path": "/transferencias"
}
```

O mesmo formato também é utilizado para erros de validação, JSON malformado,
violações de integridade, recursos inexistentes, falhas de integração e erros
inesperados. Mensagens internas do banco e stack traces não são devolvidos ao
cliente.

Principais cenários:

| Status | Situação |
|---:|---|
| `400` | JSON malformado, campos inválidos ou CPF/CNPJ inválido |
| `404` | Usuário não encontrado |
| `404` | Carteira não encontrada |
| `405` | Método HTTP não suportado |
| `409` | E-mail, identificador ou constraint duplicada |
| `415` | Tipo de conteúdo não suportado |
| `422` | Saldo insuficiente |
| `422` | Limite diário de transferência excedido |
| `503` | Cotação indisponível, timeout ou erro do Banco Central |
| `500` | Erro interno inesperado, sem exposição de detalhes |

Cada exceção de negócio possui um código estável e específico. O status HTTP
representa a categoria do erro, enquanto `codigo` permite ao cliente identificar
o cenário exato:

| Cenário | Status | Código | Mensagem |
|---|---:|---|---|
| Usuário não encontrado | `404` | `USUARIO_NAO_ENCONTRADO` | `Usuário não encontrado.` |
| Carteira não encontrada | `404` | `CARTEIRA_NAO_ENCONTRADA` | `Nenhuma carteira foi encontrada para o usuário informado.` |
| CPF/CNPJ inválido | `400` | `IDENTIFICADOR_INVALIDO` | `CPF ou CNPJ inválido.` |
| E-mail já cadastrado | `409` | `USUARIO_EMAIL_JA_CADASTRADO` | `Usuário já cadastrado com este e-mail.` |
| CPF/CNPJ já cadastrado | `409` | `USUARIO_IDENTIFICADOR_JA_CADASTRADO` | `Usuário já cadastrado com este identificador.` |
| Saldo insuficiente | `422` | `SALDO_INSUFICIENTE` | `Saldo insuficiente para realizar a transferência.` |
| Limite diário excedido | `422` | `LIMITE_DIARIO_EXCEDIDO` | `Limite diário de transferência excedido.` |
| Cotação indisponível | `503` | `COTACAO_INDISPONIVEL` | `Nenhuma cotação disponível foi encontrada.` |

## Endpoints

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/usuarios` | Cadastra usuário e cria carteira |
| `GET` | `/usuarios` | Lista usuários com paginação |
| `GET` | `/usuarios/{id}` | Consulta um usuário |
| `GET` | `/usuarios/{id}/carteira` | Consulta os saldos |
| `POST` | `/transferencias` | Realiza e registra uma transferência |

O contrato completo está em:

```text
src/main/resources/openapi/openapi.yaml
```

## Configuração local

Arquivo:

```text
src/main/resources/application.properties
```

Configuração padrão:

```properties
spring.datasource.url=jdbc:h2:mem:interdb
spring.datasource.username=sa
spring.datasource.password=
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:dbo/schema.sql
spring.sql.init.data-locations=classpath:dbo/data.sql

mybatis.mapper-locations=classpath:mappers/*.xml

integracoes.banco-central.base-url=https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata
integracoes.banco-central.dias-retroativos=7

spring.cloud.openfeign.client.config.banco-central-cotacao.connectTimeout=3000
spring.cloud.openfeign.client.config.banco-central-cotacao.readTimeout=5000
```

## Executar o projeto

Pré-requisitos:

- JDK 21;
- Maven ou Maven Wrapper.

### Compilar

Linux/macOS:

```bash
./mvnw clean package
```

Windows:

```powershell
.\mvnw.cmd clean package
```

O comando compila a aplicação, gera as classes do contrato OpenAPI e executa
os testes. O artefato final é criado no diretório `target`.

### Executar

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Serviços locais:

| Recurso | Endereço |
|---|---|
| API | `http://localhost:8080` |
| Swagger UI | `http://localhost:8080/swagger-ui/index.html` |
| Console H2 | `http://localhost:8080/h2-console` |

Dados para conexão no H2:

```text
JDBC URL: jdbc:h2:mem:interdb
User: sa
Password: vazio
```

### Executar com Docker

Pré-requisito: Docker com o plugin Docker Compose.

Construa a imagem e inicie a API:

```bash
docker compose up --build
```

Para executar em segundo plano:

```bash
docker compose up --build -d
```

A API, o Swagger UI e o console H2 ficam disponíveis nos mesmos endereços
listados acima. Para encerrar e remover o contêiner:

```bash
docker compose down
```

Também é possível usar somente o Docker:

```bash
docker build -t inter-java-challenge .
docker run --rm -p 8080:8080 inter-java-challenge
```

O banco H2 é mantido em memória; portanto, seus dados são reiniciados quando
o contêiner é recriado.

## Testes

Executar todos:

```bash
./mvnw test
```

No Windows:

```powershell
.\mvnw.cmd test
```

### Testes unitários

Cobrem:

- coordenação do fluxo de transferência;
- conversão entre real e dólar;
- definição dos valores por moeda de origem;
- débito e crédito na coluna correta;
- validação de saldo;
- validação de limite diário;
- cadastro e consulta de usuários;
- validação dos dígitos verificadores de CPF e CNPJ;
- normalização e unicidade de e-mail sem diferença entre maiúsculas e minúsculas;
- fallback da cotação de sábado e domingo;
- resposta sem cotações disponíveis.

As integrações externas e os repositórios são simulados nos testes unitários,
mantendo cada unidade isolada.

### Testes de integração

Utilizam Spring Boot, MockMvc, WireMock e instâncias H2 isoladas para validar:

- contrato HTTP do endpoint de transferência;
- integração entre business, workflows, MyBatis e banco;
- transferência de real para dólar;
- transferência de dólar para real;
- persistência do histórico;
- precisão dos valores em dólar e da cotação;
- persistência da moeda de origem;
- constraints de moeda e status;
- manutenção dos saldos quando a operação é rejeitada;
- cadastro com e-mail normalizado e rejeição de CPF inválido;
- detecção de e-mail duplicado após normalização;
- contrato HTTP gerado pelo OpenFeign, incluindo caminho, datas e parâmetros
  OData;
- desserialização da resposta simulada do Banco Central;
- fallback da cotação de sábado para a última cotação disponível.

Nos testes de transferência, a porta de busca da cotação é substituída por um
mock. No teste específico do OpenFeign, o WireMock atua como servidor HTTP
local. Assim, o contrato externo é validado sem depender de rede ou de
resultados variáveis do Banco Central.
