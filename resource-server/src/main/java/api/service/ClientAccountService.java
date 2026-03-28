package api.service;

import api.constant.ConstantValue;
import api.constant.ErrorsMessage;
import api.dto.ExtractResponseDTO;
import api.entity.AccountTransferLock;
import api.entity.Client;
import api.entity.ClientAccount;
import api.exception.InvalidProcessException;
import api.exception.NoResourceFoundException;
import api.mapper.ClientAccountMapper;
import api.repository.ClientAccountRepository;
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

    public ClientAccount save (Client client, String cpf) {
        ClientAccount clientAccount = ClientAccount.builder()
                .cpf(cpf)
                .client(client)
                .accountTransferLock(new AccountTransferLock())
                .balance(new BigDecimal(ConstantValue.LIMIT_TRANSFER_VALUE))
                .build();

        return repository.save(clientAccount);
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

        BigDecimal remainingTransferLimitValue = new BigDecimal(ConstantValue.LIMIT_TRANSFER_VALUE).subtract(clientAccount.getAccountTransferLock().getTotalValueTransferredToday());

        return mapper.toExtractResponseDTO(clientAccount, remainingTransferLimitValue);
    }

    public ClientAccount findByCpf (String cpf) {
        return repository.findByCpf(cpf)
                .orElseThrow(() -> new NoResourceFoundException(ErrorsMessage.CLIENT_ACCOUNT_NOT_FOUND, "cpf", cpf));
    }

    public ClientAccount findByClientCode (UUID clientCode) {
        return repository.findByClientCode(clientCode)
                .orElseThrow(() -> new NoResourceFoundException(ErrorsMessage.CLIENT_ACCOUNT_NOT_FOUND, "code", clientCode.toString()));
    }

    public ClientAccount findLockedById(Long id) {
        return repository.findLockedById(id)
                .orElseThrow(() -> new NoResourceFoundException(ErrorsMessage.CLIENT_ACCOUNT_NOT_FOUND, "id", id.toString()));
    }

    public boolean existsByCpf (String cpf) {
        return repository.existsByCpf(cpf);
    }

    public void deleteByClientCode (UUID code) {
        repository.deleteByClientCode(code);
    }

    public void delete (ClientAccount clientAccount) { repository.delete(clientAccount); }
}
