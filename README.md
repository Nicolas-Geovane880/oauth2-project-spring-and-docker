### Mudanças

O `Resource Server` (a API bancária) recebeu novas funcionalidades:

* Transferência: Um usuário agora pode transferir uma quantia de dinheiro para outra conta,
  porém o sistema conta com um mecanismo de limite de transferência diária.


* Extrato: O usuário autenticado pode ver o seu extrato bancário de forma segura.


* Histórico de transferências: O usuário autenticado pode ver o histórico de transferências enviadas e recebidas.
  Um Page é retornado com o histórico é enviado como resposta.

A `API gateway` está sendo modificada para que ela intercepte todas as chamadas para as outras APIs

Por enquanto, há `Controllers` iguais aos do `Resource Server `na `API gateway` para que as requisições possam
ser encaminhadas para a `Resource Server` (a API bancária).
Porém, para contornar esse retrabalho de criar todos os endpoints, irei utilizar a ferramenta Spring Cloud Gateway, que basicamente
automatiza esse processo de redirecionamento de APIs sem escrever nenhum `Controller`, apenas informando algumas configurações básicas no
arquivo de configuração.

  







