package api.service;

import api.constant.ErrorsMessage;
import api.dto.UserAccountRegisterDTO;
import api.dto.event_dto.AuthUserRegisterEventDTO;
import api.dto.event_dto.AuthUserStatusEventDTO;
import api.entity.Client;
import api.entity.Outbox;
import api.enums.Status;
import api.exception.DeserializationException;
import api.repository.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankAccountEventService {

    private final ClientService clientService;

    private final ClientAccountService clientAccountService;

    private final ObjectMapper objectMapper;

    private final RabbitTemplate rabbitTemplate;

    private final OutboxRepository outboxRepository;

    public void saveOutbox (UserAccountRegisterDTO register, UUID clientCode) {
        try {
            Outbox outbox = new Outbox(objectMapper.writeValueAsString
                    (new AuthUserRegisterEventDTO(clientCode, register.cpf(), register.password())));

            outboxRepository.save(outbox);
        } catch (Exception ex) {
            throw new DeserializationException(ErrorsMessage.DESERIALIZATION_ERROR);
        }
    }

    @Scheduled (fixedDelay = 5000)
    public void sendScheduledEvent () {
        List<Outbox> nonProcessed = outboxRepository.findByIsProcessedFalseOrderedByCreatedAtAsc();

        for (Outbox outbox : nonProcessed) {

            System.out.println(outbox.getPayload());

            try {
                Message message = MessageBuilder
                        .withBody(outbox.getPayload().getBytes(StandardCharsets.UTF_8))
                        .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                        .setMessageId(outbox.getCode().toString())
                        .build();

                rabbitTemplate.convertAndSend("auth.user.exchange", "api.user.created", message);

                outbox.setProcessed(true);
                outboxRepository.save(outbox);
            } catch (Exception ex) {
                log.warn("An error occurred while sending the event! Outbox id: '{}'. New try in 10 seconds ...", outbox.getId());
            }
        }
    }

    @RabbitListener (bindings = @QueueBinding
            (value = @Queue (value = "api.user.status.queue", durable = "true"),
            exchange = @Exchange (value = "auth.user.status.exchange", type = "topic"),
            key = "auth.user.status"))
    public void listenAuthUserCreationStatus (@Payload AuthUserStatusEventDTO eventDTO) {
        Client client = clientService.findByCode(eventDTO.clientCode());

        if (eventDTO.status().equals("SUCCESS")) {
            client.setStatus(Status.ACTIVE);
            clientService.save(client);
        }
        else {
            log.warn("The auth application fails to create the user! Reason: {}. Deleting the client with code: '{}'", eventDTO.errorCause(), eventDTO.clientCode());
            clientService.deleteByCode(eventDTO.clientCode());
            clientAccountService.deleteByClientCode(eventDTO.clientCode());
        }
    }
}
