package api.service;

import api.constant.ConstantValue;
import api.constant.ErrorsMessage;
import api.dto.ExtractResponseDTO;
import api.entity.AccountTransferLock;
import api.entity.Client;
import api.entity.ClientAccount;
import api.enums.Status;
import api.exception.InvalidProcessException;
import api.mapper.ClientAccountMapper;
import api.repository.ClientAccountRepository;
import commons.exception.NoResourceFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientAccountService {

    private final ClientAccountRepository repository;

    private final ClientAccountMapper mapper;

    public void save (Client client, String cpf) {
        ClientAccount clientAccount = ClientAccount.builder()
                .cpf(cpf)
                .client(client)
                .accountTransferLock(new AccountTransferLock())
                .balance(new BigDecimal(ConstantValue.LIMIT_TRANSFER_VALUE))
                .build();

        repository.save(clientAccount);
    }

    public void update (ClientAccount clientAccount) {
        repository.save(clientAccount);
    }

    public void softDeleteClientAccount (UUID code) {
        ClientAccount clientAccount = findByClientCode(code);

        clientAccount.setCpf("deleted.cpf" + code);
        clientAccount.setBalance(BigDecimal.ZERO);

        clientAccount.getAccountTransferLock().setTotalValueTransferredToday(BigDecimal.ZERO);

        repository.save(clientAccount);
    }

    @Transactional (readOnly = true)
    public ExtractResponseDTO extract (UUID clientCode) {
        ClientAccount clientAccount = findByClientCode(clientCode);

        if (clientAccount.getClient().getStatus() == Status.DELETED) {
            throw new InvalidProcessException(ErrorsMessage.DELETED_ACCOUNT);
        }

        BigDecimal remainingTransferLimitValue = new BigDecimal(ConstantValue.LIMIT_TRANSFER_VALUE).subtract(clientAccount.getAccountTransferLock().getTotalValueTransferredToday());

        return mapper.toExtractResponseDTO(clientAccount, remainingTransferLimitValue);
    }

    public ClientAccount findByCpf (String cpf) {
        return repository.findByCpf(cpf)
                .orElseThrow(() -> new NoResourceFoundException(ErrorsMessage.CLIENT_ACCOUNT_NOT_FOUND));
    }

    public ClientAccount findByClientCode (UUID clientCode) {
        return repository.findByClientCode(clientCode)
                .orElseThrow(() -> new NoResourceFoundException(ErrorsMessage.CLIENT_ACCOUNT_NOT_FOUND));
    }

    public ClientAccount findLockedById(Long id) {
        return repository.findLockedById(id)
                .orElseThrow(() -> new NoResourceFoundException(ErrorsMessage.CLIENT_ACCOUNT_NOT_FOUND));
    }

    public boolean existsByCpf (String cpf) {
        return repository.existsByCpf(cpf);
    }

    public void delete (ClientAccount clientAccount) { repository.delete(clientAccount); }
}
