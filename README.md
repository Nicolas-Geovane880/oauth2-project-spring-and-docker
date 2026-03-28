### Mudanças

O **CRUD** na API foi completada e está funcionando perfeitamente!

O `Update` e o `Delete` foram implementados seguindo o padrão **Saga** com eventos, assim como o `Register`.

O sistema conta com rollback robustos em casos de falhas:

`Update`:
* Quando uma requisição chega na API pelo roteamento do gateway, a conta do cliente não é
  alterada diretamente, mas os dados alterados são salvos numa tabela chamada `BankAccountUpdateHolder`
* Quando o Auth Server envia um evento dizendo que os dados do usuário foram alterados com sucesso no lado
  da autenticação, a API consulta o `BankAccountUpdateHolder` e altera os dados da conta do cliente com base nos dados salvos.
* Caso o Auth Server envie um evento com status 'FAILED', a conta do cliente não é alterada e o 
  objeto `BankAccountUpdateHolder` é removido do banco de dados.

`Delete`: 
* Quando uma requisição de DELETE chega na API, a conta do cliente não é removida diretamente.
* Quando o Auth Server envia um evento dizendo que os dados do usuário foram removidos (que no caso é um hard delete),
  a API executa um soft delete na conta do cliente.
* Caso o Auth Server envie um evento com status 'FAILED', a conta do cliente não poderá ser removida.

O sistema conta com o endpoint `/me` para que o usuário possa ver a sua conta do banco.

Todas essas funcionalidades exigem que o usuário esteja autenticado, pois funcionam via Bearer Token.

---

##### Estado Atual do Projeto

O projeto está praticamente pronto em termos de funcionalidades, basta apenas algumas padronizações e tratamentos de erros com o handler.









