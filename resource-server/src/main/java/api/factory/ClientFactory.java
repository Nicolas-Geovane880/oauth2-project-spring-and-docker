package api.factory;

import api.dto.event_dto.UserCreatedEvent;
import api.entity.AccountTransferLock;
import api.entity.Client;
import api.entity.ClientAccount;

import java.time.LocalDate;

public class ClientFactory {

    public Client newClientFromEvent (UserCreatedEvent event) {
        int age = LocalDate.now().getYear() - event.birthDate().getYear();

        return Client.builder()
                .name(event.name())
                .lastName(event.lastName())
                .email(event.email())
                .authUserId(event.authUserId())
                .birthDate(event.birthDate())
                .phone(event.phone())
                .age(age)
                .build();
    }

    public ClientAccount newClientAccountFromEvent (Client client, UserCreatedEvent event) {
        return ClientAccount.builder()
                .cpf(event.cpf())
                .client(client)
                .balance(5000.0)
                .accountTransferLock(new AccountTransferLock())
                .build();
    }
}
