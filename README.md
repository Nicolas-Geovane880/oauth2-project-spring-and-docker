## Bank-Project: Microservices Architecture

# 🏦 Bank-Project: Microservices Architecture

<p align="left">
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.3.6-brightgreen?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot 3.3.6" />
  <img src="https://img.shields.io/badge/Spring_Cloud_Gateway-Roteamento-green?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Cloud Gateway" />
  <img src="https://img.shields.io/badge/Spring_Security-OAuth2%20%2F%20JWT-6db33f?style=for-the-badge&logo=springsecurity&logoColor=white" alt="Spring Security OAuth2" />
  <br />

  <img src="https://img.shields.io/badge/Docker-Enviromnent-blue?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
  <img src="https://img.shields.io/badge/RabbitMQ-Messaging-orange?style=for-the-badge&logo=rabbitmq&logoColor=white" alt="RabbitMQ" />
  <img src="https://img.shields.io/badge/MySQL-Database-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL" />
  <img src="https://img.shields.io/badge/Flyway-Migrations-CC0202?style=for-the-badge&logo=flyway&logoColor=white" alt="Flyway" />
  <br />
</p>

Este é um ecossistema de microsserviços financeiro focado em alta disponibilidade, segurança com `OAuth2/JWT` e orientada a **eventos**!

Este projeto foi desenvolvido com o intuito de aprender como que microsserviços se comunicam em um sistema de autenticação com `OAuth2` e como eles podem se comunicar (com base nisso, eu decidi usar a orientação em eventos entre os serviços).
### Arquitetura do Sistema
O projeto é dividido em serviços especializados que se comunicam por meio de eventos com RabbitMQ:

`API Gateway` (Porta 9090): Ponto de entrada único. Faz o roteamento inteligente para os serviços internos.

`Auth Server` (Porta 9000): Servidor de autorização baseado em Spring Authorization Server. Emite tokens JWT e gerencia o login.

`API Server` (Porta 8080): O núcleo bancário. Gerencia contas, transferências e regras de negócio.

`RabbitMQ`: Broker de mensagens que garante a sincronização de dados entre a API e o Auth Server.

`MySQL`: Banco de dados relacional usada para persistir os dados.

### Padrões adotados no projeto

`Saga Pattern`: O sistema gira em torno de eventos entre a `API Server` e o `Auth Server` por meio de eventos, nas quais
seguem o padrão Saga. Todos os eventos transmitidos entre os serviços tem uma forma de compensação em casos de sucesso ou rollbacks em casos de falha.

`Outbox`: Esse padrão gerencia mensagens não processadas e permite que todas elas sejam processadas pelo RabbitMQ, mesmo que de forma assíncrona. Esse padrão que roda em segundo plano de forma agendada, onde executa a lógica a cada 5 segundos.

---

#### O que o sistema faz?

O projeto é um CRUD que simula um banco, na qual possui funcionalidades como:

* Fazer transferências entre contas. O sistema conta com um limite diário de transferências.
* Ver o extrato bancário da conta do usuário autenticado.
* Ver o histórico de transações do usuário autenticado.

O sistema conta com validações robustas de campo e de lógica, fazendo o sistema íntegro em relação aos dados.

---
### Tecnologias

* Java 21
* Spring Boot 3 & Spring Cloud Gateway
* Spring Security OAuth2
* RabbitMQ
* MySQL
* Flyway
* Testcontainers e Rest Assured
* Docker-compose

---
### Como rodar

Todas as requisições foram feitas através do Postman, portanto você deve obter um token via OAuth2:

Crie uma guia, não mude o método HTTP e não coloque nada na URL, apenas vá na aba de Authorizations e selecione a opção `OAuth2`

Grant Type: Authorization Code
* Callback URL:  `https://oauth.pstmn.io/v1/callback`
* Auth URL: `http://localhost:9000/oauth2/authorize`
* Access Token URL: `http://localhost:9000/oauth2/token`
* Client ID / Secret: ``postman-client`` / ``postman-secret``
* Scopes: openid profile api.read api.write (são scopes de exemplo apenas)

#### Configurar o ambiente Docker

O projeto foi criado para rodar exclusivamente no Docker-compose, portanto, é necessário que voce tenha
o Docker Desktop instalado e rodando na sua máquina.

* Crie o seu .env baseado nas variáveis de ambiente presente no .env-examples (ou defina a configuração do banco diretamente no application.yml de cada serviço).
* Rode `mvn clean install -DskipTests` para criar o .jar
* Abra o terminal no root do projeto
* Rode `docker-compose up --build id`
* Verifique se todos os serviços subiram com `docker ps`

#### Testando as requisições

* Abra uma guia no Postman e digite na url `http://localhost:9090/api/v1/account/`
* Coloque o body (formato JSON, método POST):
```
{
  "name": "Example name",
  "lastName": "Example last name",
  "cpf": "11122233344",
  "password": "senhasenha",
  "email": "Example@email.com",
  "birthDate": "2000-01-01",
  "phone": "(00) 9999-9999"
}
```
* Na aba de autorização (definida anteriormente com o OAuth2), desça a página até o fim e clique no botão 'Get new access Token'
* Aparecerá uma janela para que voce possa fazer login (no username, coloque: 11122233344 | na senha, coloque: senhasenha)
* O Postman irá mostrar o Token, então o copie por completo.
* Abra outra guia e digite na url `http://localhost:9090/api/v1/account/me` com método GET
* Na aba authorization, selecione a opção `Bearer Token` e cole o Token copiado anteriormente.
* Caso tudo ocorrá como esperado, o perfil do usuário criado aparecerá na sua tela.
* Certifique-se de parar os serviços após os testes. No terminal na pasta root, digite `docker-compose down`

---
Criado por: Nicolas Geovane Mateus




