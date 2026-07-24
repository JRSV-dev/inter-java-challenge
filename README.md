# Inter Java Challenge — API de Remessas

API REST para cadastro de usuários e transferências entre carteiras com
saldos em real e dólar.

O contrato HTTP é definido em OpenAPI, a persistência utiliza MyBatis com H2
e a cotação do dólar é consultada no Banco Central por meio de OpenFeign.

## Tecnologias

- Java 21
- Spring Boot 3.5
- Spring Cloud OpenFeign
- Spring Validation e Spring Transaction
- MyBatis e H2
- MapStruct e Lombok
- OpenAPI e Swagger UI
- JUnit 5, Mockito, AssertJ e MockMvc

## Estrutura

```text
services/       implementação dos endpoints OpenAPI
business/       coordenação dos casos de uso
workflows/      validações, conversão e movimentação dos saldos
client/         integração OpenFeign com o Banco Central
repository/     contratos de persistência MyBatis
data/           modelos, records e enums
```

O fluxo principal de uma transferência é:

```text
Requisição
  → busca da cotação
  → bloqueio das carteiras
  → validação de saldo e limite
  → débito da origem
  → crédito do destino
  → registro da transferência
```

## Regras de transferência

- A carteira mantém saldos separados em real e dólar.
- `REAL` debita reais da origem e credita o valor convertido em dólares.
- `DOLAR` debita dólares da origem e credita o valor convertido em reais.
- Pessoa Física possui limite diário de R$ 10.000,00.
- Pessoa Jurídica possui limite diário de R$ 50.000,00.
- O saldo disponível é validado na moeda de origem.
- A operação é executada dentro de uma transação do banco de dados.
- As carteiras são bloqueadas durante a movimentação para evitar alterações
  concorrentes.

## Cotação do Banco Central

A chamada externa está declarada em
`BancoCentralCotacaoFeignClient`.

O serviço solicita os campos `cotacaoCompra` e `dataHoraCotacao`, ordena os
resultados pela data mais recente e limita a resposta a um registro.

A janela de consulta é de sete dias. Portanto, aos sábados, domingos e
feriados, é utilizada a última cotação disponível — normalmente a cotação da
sexta-feira.

Se nenhuma cotação for encontrada, a API retorna:

```text
503 Service Unavailable
```

## Endpoints

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/usuarios` | Cadastra um usuário |
| `GET` | `/usuarios` | Lista usuários |
| `GET` | `/usuarios/{id}` | Consulta um usuário |
| `GET` | `/usuarios/{id}/carteira` | Consulta a carteira |
| `POST` | `/transferencias` | Realiza uma transferência |

## Exemplo de transferência

```http
POST /transferencias
Content-Type: application/json
```

```json
{
  "usuarioOrigemId": 1,
  "usuarioDestinoId": 2,
  "moedaOrigem": "REAL",
  "valor": 100.00
}
```

Valores aceitos em `moedaOrigem`:

- `REAL`
- `DOLAR`

## Executar localmente

Pré-requisitos:

- JDK 21
- Maven, ou o Maven Wrapper incluído no projeto

No Linux/macOS:

```bash
./mvnw spring-boot:run
```

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

Console H2:

```text
http://localhost:8080/h2-console
```

## Configuração

As principais propriedades estão em `src/main/resources/application.properties`:

```properties
integracoes.banco-central.base-url=https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata
integracoes.banco-central.dias-retroativos=7
```

## Testes

```bash
./mvnw test
```

A suíte possui:

- testes unitários dos casos de uso, conversões e validações;
- testes do adaptador que utiliza o cliente Feign;
- testes de integração com Spring Boot, MockMvc e H2.
