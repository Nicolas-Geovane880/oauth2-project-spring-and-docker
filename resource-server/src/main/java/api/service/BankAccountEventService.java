package api.service;

import api.constant.ErrorsMessage;
import api.dto.BankAccountProcessDTO;
import api.dto.UserAccountUpdateDTO;
import api.entity.BankAccountUpdateHolder;
import api.entity.ClientAccount;
import api.entity.Outbox;
import api.enums.Status;
import api.exception.DeserializationException;
import api.mapper.BankAccountMapper;
import api.repository.BankAccountUpdateHolderRepository;
import api.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.dto.AuthUserEventDTO;
import commons.dto.AuthUserStatusEventDTO;
import commons.enums.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankAccountEventService {

    private final ClientService clientService;

    private final ClientAccountService clientAccountService;

    private final BankAccountUpdateHolderRepository bankAccountUpdateHolderRepository;

    private final OutboxRepository outboxRepository;

    private final BankAccountMapper mapper;

    private final ObjectMapper objectMapper;

    private final RabbitTemplate rabbitTemplate;

    @Value (value = "${api_user_created_key}")
    private String apiUserCreatedKey;

    @Value (value = "${api_user_updated_key}")
    private String apiUserUpdatedKey;

    @Value (value = "${api_user_deleted_key}")
    private String apiUserDeletedKey;

    @Value (value = "${api_exchange}")
    private String apiExchange;

    public void saveOutbox (BankAccountProcessDTO process, UUID clientCode, EventType eventType) {
        String cpf = process != null ? process.cpf() : null;
        String password = process != null ? process.password() : null;

        AuthUserEventDTO event = new AuthUserEventDTO(clientCode, cpf, password);

        try {
            Outbox outbox = new Outbox(objectMapper.writeValueAsString(event), eventType);

            outboxRepository.save(outbox);
        } catch (Exception ex) {
            throw new DeserializationException(ErrorsMessage.DESERIALIZATION_ERROR);
        }
    }

    public void saveBankAccountUpdateHolder (UserAccountUpdateDTO updateDTO, UUID clientCode) {
        BankAccountUpdateHolder bankAccountUpdateHolder = mapper.toBankAccountUpdateHolder(updateDTO);
        bankAccountUpdateHolder.setClientCode(clientCode);

        bankAccountUpdateHolderRepository.save(bankAccountUpdateHolder);
    }

    @Scheduled (fixedDelay = 5000)
    public void sendScheduledEvent () {
        List<Outbox> nonProcessed = outboxRepository.findByIsProcessedFalseOrderedByCreatedAtAsc();

        for (Outbox outbox : nonProcessed) {
            try {
                Message message = MessageBuilder
                        .withBody(outbox.getPayload().getBytes(StandardCharsets.UTF_8))
                        .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                        .setMessageId(outbox.getCode().toString())
                        .build();

                switch (outbox.getEventType()) {
                    case EventType.USER_ACCOUNT_REGISTERED -> rabbitTemplate.convertAndSend(apiExchange, apiUserCreatedKey, message);

                    case EventType.USER_ACCOUNT_UPDATED -> rabbitTemplate.convertAndSend(apiExchange, apiUserUpdatedKey, message);

                    case EventType.USER_ACCOUNT_DELETED -> rabbitTemplate.convertAndSend(apiExchange, apiUserDeletedKey, message);
                }

                outbox.setProcessed(true);
                outboxRepository.save(outbox);
            } catch (Exception ex) {
                log.warn("An error occurred while sending the event! Outbox id: '{}'. New try in 10 seconds ...", outbox.getId());
            }
        }
    }

    @Transactional
    @RabbitListener (bindings = @QueueBinding
            (value = @Queue (value = "${api_created_queue}", durable = "true"),
             exchange = @Exchange (value = "${auth_exchange}", type = "topic"),
             key = "${auth_user_created_key}"))
    public void listenAuthUserCreatedEvent (@Payload AuthUserStatusEventDTO eventDTO) {
        ClientAccount clientAccount = clientAccountService.findByClientCode(eventDTO.clientCode());

        processListenedEvent
                (eventDTO,
                "Register",
                () -> clientAccount.getClient().getStatus() == Status.ACTIVE,
                () -> {
                    clientAccount.getClient().setStatus(Status.ACTIVE);
                    clientService.save(clientAccount.getClient());
                },
                () -> {
                    log.warn("The auth application failed to create the user! Reason: {}. Deleting the client with code: '{}'", eventDTO.errorCause(), eventDTO.clientCode());
                    clientAccountService.delete(clientAccount);
                    clientService.delete(clientAccount.getClient());
                });
    }

    @Transactional
    @RabbitListener (bindings = @QueueBinding
            (value = @Queue (value = "${api_updated_queue}", durable = "true"),
             exchange = @Exchange (value = "${auth_exchange}", type = "topic"),
             key = "${auth_user_updated_key}"))
    public void listenAuthUserUpdatedEvent (@Payload AuthUserStatusEventDTO eventDTO) {
        BankAccountUpdateHolder bankAccountUpdateHolder = bankAccountUpdateHolderRepository.findByClientCode(eventDTO.clientCode())
                .orElse(null);

        processListenedEvent
                (eventDTO,
                "Update",
                () -> bankAccountUpdateHolder == null,
                () -> {
                    ClientAccount clientAccount = clientAccountService.findByClientCode(eventDTO.clientCode());
                    mapper.update(bankAccountUpdateHolder, clientAccount);
                    clientAccountService.update(clientAccount);
                    clientService.save(clientAccount.getClient());
                    bankAccountUpdateHolderRepository.delete(bankAccountUpdateHolder);
                },
                () -> {
                    log.warn("The auth application failed to update the user! Reason: {}. The client account can not be updated! Client code: '{}'", eventDTO.errorCause(), eventDTO.clientCode());
                    bankAccountUpdateHolderRepository.delete(bankAccountUpdateHolder);
                });
    }

    @Transactional
    @RabbitListener (bindings = @QueueBinding
            (value = @Queue (value = "${api_deleted_queue}", durable = "true"),
             exchange = @Exchange (value = "${auth_exchange}", type = "topic"),
             key = "${auth_user_deleted_key}"))
    public void listenAuthUserDeletedEvent (@Payload AuthUserStatusEventDTO eventDTO) {
        ClientAccount clientAccount = clientAccountService.findByClientCode(eventDTO.clientCode());

        processListenedEvent
               (eventDTO,
               "Delete",
               () -> clientAccount.getClient().getStatus() == Status.DELETED,
               () -> {
                   clientAccountService.softDeleteClientAccount(eventDTO.clientCode());
                   clientService.softDeleteClient(eventDTO.clientCode());
               },
               () -> log.warn("The auth application failed to delete the user! Reason: {}. The client account can not be deleted! Client code: '{}'", eventDTO.errorCause(), eventDTO.clientCode()));
    }

    private void processListenedEvent (AuthUserStatusEventDTO eventDTO,
                                       String eventType,
                                       Supplier<Boolean> isProcessed,
                                       Runnable onSuccess,
                                       Runnable onFailed) {

        if (isProcessed.get()) {
            log.warn("Message event of type '{}' is already processed! Client code: '{}'", eventType, eventDTO.clientCode());
            return;
        }

        switch (eventDTO.status()) {

            case "SUCCESS" -> onSuccess.run();

            case "FAILED" -> onFailed.run();
        }
    }
}
