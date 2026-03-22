package api.service;

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
                .balance(new BigDecimal("5000.00"))
                .build();

        return repository.save(clientAccount);
    }

    @Transactional (readOnly = true)
    public ExtractResponseDTO extract (Long authUserId) {
        ClientAccount clientAccount = findByClientAuthUserId(authUserId);

        BigDecimal remainingTransferLimitValue = new BigDecimal("5000.00").subtract(clientAccount.getAccountTransferLock().getTotalValueTransferredToday());

        return mapper.toExtractResponseDTO(clientAccount, remainingTransferLimitValue);
    }

    public ClientAccount findByCpf (String cpf) {
        return repository.findByCpf(cpf)
                .orElseThrow(() -> new NoResourceFoundException("Client account not found"));
    }

    public ClientAccount findByClientAuthUserId (Long authUserId) {
        return repository.findByClientAuthUserId(authUserId)
                .orElseThrow(() -> new NoResourceFoundException("Client account not found"));
    }

    public ClientAccount findLockedById(Long id) {
        return repository.findLockedById(id)
                .orElseThrow(() -> new NoResourceFoundException("Client account not found"));
    }
}
