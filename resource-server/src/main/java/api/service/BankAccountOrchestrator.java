package api.service;

import api.constant.ErrorsMessage;
import api.dto.UserAccountResponseDTO;
import api.dto.event_dto.AuthUserRegisterEventDTO;
import api.dto.UserAccountRegisterDTO;
import api.entity.Client;
import api.entity.Outbox;
import api.exception.ConflictFieldsException;
import api.mapper.AccountRegisterMapper;
import api.repository.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BankAccountOrchestrator {

    private final ClientService clientService;

    private final ClientAccountService clientAccountService;

    private final AccountRegisterMapper mapper;

    private final BankAccountEventService eventService;

    @Transactional
    public UserAccountResponseDTO saveClientAndAccount (UserAccountRegisterDTO register) {
        handleValidations(register);

        Client savedClient = clientService.save(mapper.toClient(register));

        clientAccountService.save(savedClient, register.cpf());

        eventService.saveOutbox(register, savedClient.getCode());

        return mapper.toUserAccountResponseDTO(savedClient, register.cpf());
    }

    private void handleValidations (UserAccountRegisterDTO register) {
        Map<String, String> conflicts = new HashMap<>();

        if (clientService.existsByPhone(register.phone())) {
            conflicts.put("phone", "given phone is already in use");
        }
        if (clientService.existsByEmail(register.email())) {
            conflicts.put("email", "given email is already in use");
        }
        if (clientAccountService.existsByCpf(register.cpf())) {
            conflicts.put("cpf", "given cpf is already in use");
        }

        if (!conflicts.isEmpty()) {
            throw new ConflictFieldsException(ErrorsMessage.CONFLICTED_FIELD, conflicts);
        }
    }


}
