### Mudanças

O sistema agora está padronizado em todas as partes, com tratamentos de erro e mensagens padronizadas!

A maior mudança foi a criação do módulo `Commons`, feita para desacoplar os serviços em relação aos `DTOs` de eventos.

* Todos os `Dtos` de evento (que estavam presente tanto na API, quanto no Auth Server), agora estão nesse módulo.
* Algumas exceções também foram transportadas para o `Commons`.

Dessa maneira, os 2 serviços podem importar os `DTOs` e exceções do `Commons`

O projeto está oficialmente pronto, o próximo commit irá apenas ajeitar o ambiente para que se possa rodar no docker-compose.









