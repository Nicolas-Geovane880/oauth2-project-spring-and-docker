### Mudanças

A `API gateway` agora usa o mecanismo do Spring Cloud Gateway, substituindo todos os `Controllers` e
camadas de `Serviço` que faziam chamadas `HTTP` para os outros serviços. Agora, o gateway apenas
roteia as requisições conforme a `URL` da requisição.

### O processo de registro do usuário foi **reformulada** !

O registro do usuário segue o padrão chamado `SAGA`:

* A `API gateway`, por meio do **Spring Cloud Gateway**, roteia a requisição para o `Resource Server`.


* O `Resource Server` (a API bancária que carrega o domínio), valida os inputs, cadastra o cliente e manda um **evento**
  com `RabbitMQ` dizendo que o cliente foi criado.


* O `Authentication Server` (a API responsável pelo login), escuta que esse evento ocorreu, e cria o usuário de autentição com os dados
  transmitidos por meio da mensagem do evento (o payload).


* Como o `Saga Pattern` exige uma forma de compensar os primeiros processos, o `Authentication Server` manda outro **evento**
  dizendo sobre a condição da criação do usuário:

  - Caso crie o usuário de autenticação com sucesso, um evento é disparado com o status 'SUCCESS'. O `Resource Server`, recebe esse
    evento e atualiza o status do cliente para 'ACTIVE'.
  
  - Caso a criação falhe, um evento é disparado com o status 'FAILED'. O `Resource Server` recebe o evento e inicializa
  o rollback, excluindo o cliente do banco de dados para não ter erros com colunas UNIQUES em registros futuros. 
  
O fato de retirar a lógica de registro da `API gateway`, foi necessário mover toda a validação de inputs
para o `Resource Server`, acrescentando mais responsabilidades sobre a API, tornando-a sobrecarregada.

### Próximas mudanças

* Implementar algumas padronizações em todo o código
* Implementar o UPDATE e DELETE
* Implementar o limite de tentativas de login





