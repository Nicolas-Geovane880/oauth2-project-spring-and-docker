package api.service;

import api.constant.ErrorsMessage;
import api.dto.BankAccountProcessDTO;
import api.dto.UserAccountResponseDTO;
import api.dto.BankAccountRegisterDTO;
import api.dto.UserAccountUpdateDTO;
import api.entity.BankAccountUpdateHolder;
import api.entity.Client;
import api.entity.ClientAccount;
import api.enums.EventType;
import api.exception.ConflictFieldsException;
import api.exception.InvalidProcessException;
import api.mapper.BankAccountMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankAccountService {

    private final ClientService clientService;

    private final ClientAccountService clientAccountService;

    private final BankAccountMapper mapper;

    private final BankAccountEventService eventService;

    @Transactional
    public UserAccountResponseDTO registerBankAccount(BankAccountRegisterDTO register) {
        handleValidations(register);

        Client savedClient = clientService.save(mapper.toClient(register));

        clientAccountService.save(savedClient, register.cpf());

        eventService.saveOutbox(register, savedClient.getCode(), EventType.USER_ACCOUNT_REGISTERED);

        return mapper.toUserAccountResponseDTO(savedClient, register.cpf());
    }

    @Transactional (readOnly = true)
    public UserAccountResponseDTO me (UUID clientCode) {
        ClientAccount clientAccount = clientAccountService.findByClientCode(clientCode);

        return mapper.toUserAccountResponseDTO(clientAccount.getClient(), clientAccount.getCpf());
    }

    @Transactional
    public void update (UUID clientCode, UserAccountUpdateDTO updateDTO) {
        handleValidations(updateDTO);

        BankAccountUpdateHolder bankAccountUpdateHolder = mapper.toBankAccountUpdateHolder(updateDTO);
        bankAccountUpdateHolder.setClientCode(clientCode);

        eventService.saveOutbox(updateDTO, clientCode, EventType.USER_ACCOUNT_UPDATED);

        eventService.saveBankAccountUpdateHolder(updateDTO, clientCode);
    }

    @Transactional
    public void softDeleteBankAccount (UUID clientCode) {
        ClientAccount clientAccount = clientAccountService.findByClientCode(clientCode);

        if (clientAccount.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new InvalidProcessException("Make sure to take out your balance before delete the account");
        }

        eventService.saveOutbox(null, clientCode, EventType.USER_ACCOUNT_DELETED);
    }

    private void handleValidations (BankAccountProcessDTO process) {
        Map<String, String> conflicts = new HashMap<>();

        if (clientService.existsByPhone(process.phone())) {
            conflicts.put("phone", "given phone is already in use");
        }
        if (clientService.existsByEmail(process.email())) {
            conflicts.put("email", "given email is already in use");
        }
        if (clientAccountService.existsByCpf(process.cpf())) {
            conflicts.put("cpf", "given cpf is already in use");
        }

        if (!conflicts.isEmpty()) {
            throw new ConflictFieldsException(ErrorsMessage.CONFLICTED_FIELD, conflicts);
        }
    }


}
