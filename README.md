### Mudanças

Agora a `API Gateway` está funcionando como deveria, recebendo a requisição e interceptando as chamadas
para as outras APIs.

* A `API gateway` agora tem uma validação robusta nas entidades de registro com Bean Validation.

* O sistema conta com a padronização das mensagens de erro com o `messages.properties`.


* Agora o sistema tem o motor base para o funcionamento por eventos/mensagem com `RabbitMQ`, e essa
  funcionalidade irá ser mais utilizada conforme o projeto avança.

A forma na qual o rollback acontece foi mudada:

* Anteriormente, quando o `Resource Server` falhava na criação do usuário (domínio da API), o **rollback** era feito
 para apagar o usuário no `Authentication Server` (usuário de autenticação) para que ele não ficasse órfão no banco de dados. Porém,
 para que isso acontecesse, o endpoint de apagar o usuário pelo seu ID no `Authentication Server` devia permanecer aberta.


* Dado que isso é inseguro quando falamos em tráfegos pela rede, eu decidi realizar esse **rollback** com **mensageria** utilizando o `RabbitMQ`. Dessa forma
o **rollback** estará definido mesmo quando o servidor estiver fora de ar, e o melhor é que barramos o endpoint de excluir o usuário pelo seu ID.

####  Próximas atualizações:

* Fazer com que a API gateway intercepte todas as requisições, assim as portas 
expostas de cada API poderá ser fechada, aumentando o grau de segurança.


* Concluir o `Resource Server` (a API que carrega o domínio) e suas funcionalidades.


* Aumentar a segurança no `Authentication Server`, limitando a quantidade de tentativas de login por exemplo.







