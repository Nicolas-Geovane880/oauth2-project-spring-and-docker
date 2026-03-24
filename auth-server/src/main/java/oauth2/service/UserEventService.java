package oauth2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oauth2.dto.events_dto.AuthUserRegisterEventDTO;
import oauth2.dto.events_dto.AuthUserStatusEventDTO;
import oauth2.entity.ProcessedEvent;
import oauth2.repository.ProcessedEventRepository;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventService {

    private final UserService userService;

    private final ProcessedEventRepository processedEventRepository;

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener (bindings = @QueueBinding
            (value = @Queue (value = "auth.create.queue", durable = "true"),
            exchange = @Exchange (value = "auth.user.exchange", type = "topic"),
            key = "api.user.created"))
    public void registerUserFromEvent (@Payload AuthUserRegisterEventDTO eventDTO,
                                       @Header(AmqpHeaders.MESSAGE_ID) String outboxCodeStr) {
        UUID outboxCode = UUID.fromString(outboxCodeStr);

        Optional<ProcessedEvent> optProcessedEvent = processedEventRepository.findByCode(outboxCode);

        if (optProcessedEvent.isPresent()) {
            log.warn("Event already processed! Processed event ID: '{}'", optProcessedEvent.get().getId());
            return;
        }

        try {
            userService.register(eventDTO);
            sentAuthUserCreationStatus("SUCCESS", null, eventDTO.clientCode());

            ProcessedEvent processedEvent = new ProcessedEvent("AUTH_USER_CREATED", outboxCode);
            processedEventRepository.save(processedEvent);

        } catch (Exception ex) {
            log.warn("An unexpected error occurred while registering the user in the Authentication Server! Client code: '{}'. Sending rollback ...", eventDTO.clientCode());
            sentAuthUserCreationStatus("FAILED", ex.getMessage(), eventDTO.clientCode());
        }
    }

    public void sentAuthUserCreationStatus (String status, String errorCause, UUID clientCode) {
        AuthUserStatusEventDTO event = new AuthUserStatusEventDTO(status, errorCause, clientCode);

        rabbitTemplate.convertAndSend("auth.user.status.exchange", "auth.user.status", event);
    }
}
