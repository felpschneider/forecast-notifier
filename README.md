# Notificador de Previsão do Tempo

## Visão Geral

Este projeto foi desenvolvido com o objetivo de criar um sistema de notificações de previsão do tempo para usuários,
consumindo dados do CPTEC (Centro de Previsão de Tempo e Estudos Climáticos).

O sistema permite que usuários se cadastrem e assinem notificações sobre previsões do tempo para cidades específicas,
com agendamentos personalizados. Para cidades litorâneas, o sistema também fornece informações sobre ondas.

## Diagrama de Arquitetura

### Diagrama de Alto Nível

[Espaço para inserir diagrama de arquitetura]

### Fluxo de Dados

[Espaço para inserir diagrama de fluxo de dados]

## Tecnologias Utilizadas

- **Backend**: Java 21 + Spring Boot 3.4
- **Banco de Dados**: PostgreSQL
- **Cache e Locks Distribuídos**: Redis
- **Mensageria**: Apache Kafka
- **Agendamento Distribuído**: Quartz Scheduler + ShedLock
- **Notificações Web**: WebSockets
- **Containerização**: Docker e Docker Compose

## Decisões Arquiteturais

O sistema foi projetado para ser escalável e resiliente, seguindo princípios de arquitetura hexagonal (ports and
adapters). Algumas decisões arquiteturais importantes:

1. **Monolito Modular**: Inicialmente implementado como monolito, com fronteiras entre módulos, facilitando eventual
   migração para microsserviços.

2. **Cache Distribuído**: Uso de Redis para cache de dados do CPTEC.

3. **Mensageria**: Apache Kafka para processamento assíncrono de notificações, garantindo escalabilidade e resiliência.

4. **Agendamento Distribuído**: Quartz para garantir que jobs sejam executados apenas uma vez em ambiente distribuído.

5. **Comunicação em Tempo Real**: WebSockets para entrega de notificações instantâneas aos usuários.

6. **Design Patterns**: Uso do Strategy Pattern para permitir a fácil adição de novos canais de notificação no futuro.

## Como Executar

### Pré-requisitos

- Java 21
- Maven
- Docker e Docker Compose

### Iniciar a Aplicação

1. Clone o repositório:

```bash
git clone https://github.com/felpschneider/notifier-forecast.git
cd notifier-forecast
```

2. Execute o Docker Compose:

```bash
docker-compose up -d
```

A aplicação estará disponível em `http://localhost:8080`

## Fluxo de Uso

### 1. Registro de Usuário

Endpoint: `POST /users/register`

Exemplo de requisição:

```json
{
  "name": "João Silva",
  "email": "joao@example.com",
  "phoneNumber": "+5511999999999",
  "password": "senha123"
}
```

[Espaço para screenshot do Postman]

### 2. Login

Endpoint: `POST /users/login`

Exemplo de requisição:

```json
{
  "email": "joao@example.com",
  "password": "senha123"
}
```

Resposta:

```json
{
  "token": "jwt-token-here",
  "expirationDate": "expiration"
}
```

[Espaço para screenshot do Postman]

### 3. Buscar Cidades

Endpoint: `GET /cities/search?name=rio de janeiro`

Resposta:

```json
[
  {
    "idCptec": 241,
    "name": "Rio de Janeiro",
    "stateCode": "SP",
    "isCoastal": false
  }
]
```

[Espaço para screenshot do Postman]

### 4. Criar Assinatura

Endpoint: `POST /subscriptions`

Exemplo de requisição:

```json
{
  "cityId": 241, // Cidade do Rio de Janeiro
  "cronExpression": "0 * * * * ?" // Roda a cada minuto
}
```

[Espaço para screenshot do Postman]

### 5. Conectar ao WebSocket

URL: `ws://localhost:8080/ws`

Para autenticar, adicione o header de conexão:

```
Authorization: Bearer <jwt-token>
```
[Espaço para screenshot do Postman WebSocket]

## Estrutura do Projeto

A aplicação segue uma estrutura baseada em arquitetura hexagonal:

```
- adapter: Adaptadores de entrada e saída
    - in: adaptadores de entrada
        - web (controladores REST)
        - websocket (controladores WebSocket)
        - scheduler (jobs agendados)
    - out (adaptadores de saída)
        - persistence (repositórios JPA)
        - messaging (produtores Kafka)
        - integration (clientes HTTP para o CPTEC)
- application
    - port
        - in (portas de entrada - interfaces de serviço)
        - out (portas de saída - interfaces de repositório e outros)
    - service (implementações de serviço)
- config: Configurações gerais
- domain
    - model (modelos de domínio)
    - exception (exceções de negócio)
```

## Melhorias Futuras

- Implementação de novos canais de notificação (SMS, Push, Email)
- Dashboard para gerenciamento de assinaturas
- Expansão de métricas e monitoramento
- Interface de usuário para visualização de previsões
- Testes automatizados (unitários e de integração)