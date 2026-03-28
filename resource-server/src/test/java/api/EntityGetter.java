package api;

import api.dto.ExtractResponseDTO;
import api.dto.TransferResponseDTO;
import api.entity.AccountTransferLock;
import api.entity.Client;
import api.entity.ClientAccount;
import api.enums.Status;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.C;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class EntityGetter {

    public Client getClient () {
        return Client.builder()
                .code(UUID.randomUUID())
                .name("name")
                .lastName("last name")
                .email("Client@mail.com")
                .phone("(xx) 1234-5678")
                .age(20)
                .birthDate(LocalDate.now().minus(20, ChronoUnit.YEARS))
                .status(Status.ACTIVE)
                .build();
    }

    public ClientAccount getClientAccount (Client client) {
        return ClientAccount.builder()
                .cpf("12345678900")
                .client(client)
                .accountTransferLock(getAccountTransferLock())
                .balance(new BigDecimal("1000.00"))
                .build();
    }

    public AccountTransferLock getAccountTransferLock () {
        return AccountTransferLock.builder()
                .lastTransferDate(null)
                .totalValueTransferredToday(BigDecimal.ZERO)
                .build();
    }

    public TransferResponseDTO getTransferResponseDTO (ClientAccount source,
                                                       ClientAccount target,
                                                       BigDecimal value,
                                                       String anonymizedSourceCPF,
                                                       String anonymizedTargetCPF) {
        return TransferResponseDTO.builder()
                .sourceCPF(anonymizedSourceCPF)
                .sourceName(source.getClient() == null ? null : source.getClient().getName())
                .targetCPF(anonymizedTargetCPF)
                .targetName(target.getClient() == null ? null : target.getClient().getName())
                .value(value)
                .build();
    }

    public ExtractResponseDTO getExtractResponseDTO (ClientAccount clientAccount) {
        return ExtractResponseDTO.builder()
                .name(clientAccount.getClient() == null ? null : clientAccount.getClient().getName() + " " + clientAccount.getClient().getLastName())
                .cpf(clientAccount.getCpf())
                .balance(clientAccount.getBalance())
                .remainingTransferLimitValue(new BigDecimal("2000.00"))
                .build();
    }

}
