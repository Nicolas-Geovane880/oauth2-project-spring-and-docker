package api.service;

import api.constant.ConstantValue;
import api.constant.ErrorsMessage;
import api.dto.ExtractResponseDTO;
import api.entity.AccountTransferLock;
import api.entity.Client;
import api.entity.ClientAccount;
import api.exception.NoResourceFoundException;
import api.mapper.BankAccountMapper;
import api.repository.ClientAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientAccountService {

    private final ClientAccountRepository repository;

    private final BankAccountMapper mapper;

    public ClientAccount save (Client client, String cpf) {
        ClientAccount clientAccount = ClientAccount.builder()
                .cpf(cpf)
                .client(client)
                .accountTransferLock(new AccountTransferLock())
                .balance(new BigDecimal(ConstantValue.LIMIT_TRANSFER_VALUE))
                .build();

        return repository.save(clientAccount);
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
}
