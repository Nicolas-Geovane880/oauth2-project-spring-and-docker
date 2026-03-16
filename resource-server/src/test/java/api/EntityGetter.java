package api;

import api.entity.AccountTransferLock;
import api.entity.Client;
import api.entity.ClientAccount;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.C;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class EntityGetter {

    public Client getClient () {
        return Client.builder()
                .name("name")
                .lastName("last name")
                .email("Client@mail.com")
                .phone("(xx) 1234-5678")
                .age(20)
                .birthDate(LocalDate.now().minus(20, ChronoUnit.YEARS))
                .build();
    }

    public ClientAccount getClientAccount (Client client) {
        return ClientAccount.builder()
                .cpf("12345678900")
                .client(client)
                .accountTransferLock(new AccountTransferLock())
                .balance(1000.0)
                .build();
    }

}
