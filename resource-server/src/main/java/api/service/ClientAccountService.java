package api.service;

import api.entity.AccountTransferLock;
import api.entity.Client;
import api.entity.ClientAccount;
import api.repository.ClientAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientAccountService {

    private final ClientAccountRepository repository;

    public ClientAccount save (Client client, String cpf) {
        ClientAccount clientAccount = ClientAccount.builder()
                .cpf(cpf)
                .client(client)
                .accountTransferLock(new AccountTransferLock())
                .balance(5_000D)
                .build();

        return repository.save(clientAccount);
    }


}
