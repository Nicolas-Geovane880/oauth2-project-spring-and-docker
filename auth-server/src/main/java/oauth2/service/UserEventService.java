package oauth2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oauth2.dto.events_dto.AuthUserEventDTO;
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
            (value = @Queue (value = "auth.user.created.queue", durable = "true"),
             exchange = @Exchange (value = "api.user.exchange", type = "topic"),
             key = "api.user.created"))
    public void registerUserFromEvent (@Payload AuthUserEventDTO eventDTO,
                                       @Header(AmqpHeaders.MESSAGE_ID) String outboxCodeStr) {
        String routingKey = "auth.user.created";

        processEvent(eventDTO, outboxCodeStr, routingKey, () -> userService.register(eventDTO));
    }

    @RabbitListener (bindings = @QueueBinding
            (value = @Queue (value = "auth.user.updated.queue", durable = "true"),
             exchange = @Exchange (value = "api.user.exchange", type = "topic"),
             key = "api.user.updated"))
    public void updateUserFromEvent (@Payload AuthUserEventDTO eventDTO,
                                     @Header(AmqpHeaders.MESSAGE_ID) String outboxCodeStr) {
        String routingKey = "auth.user.updated";

        processEvent(eventDTO, outboxCodeStr, routingKey, () -> userService.update(eventDTO));
    }

    @RabbitListener (bindings = @QueueBinding
            (value = @Queue (value = "auth.user.deleted.queue", durable = "true"),
             exchange = @Exchange (value = "api.user.exchange", type = "topic"),
             key = "api.user.deleted"))
    public void deleteUserFromEvent (@Payload AuthUserEventDTO eventDTO,
                                     @Header(AmqpHeaders.MESSAGE_ID) String outboxCodeStr) {
        String routingKey = "auth.user.deleted";

        processEvent(eventDTO, outboxCodeStr, routingKey, () -> userService.delete(eventDTO.clientCode()));
    }

    private boolean isEventProcessed (UUID outboxCode) {
        Optional<ProcessedEvent> optProcessedEvent = processedEventRepository.findByCode(outboxCode);

        if (optProcessedEvent.isPresent()) {
            log.warn("Event already processed! Processed event ID: '{}'", optProcessedEvent.get().getId());
            return true;
        }

        return false;
    }

    private String handleExceptionMessage (Exception ex) {
        String errorMessage = ex.getMessage();

        if (errorMessage == null && ex.getCause() != null) {
            errorMessage = ex.getCause().getMessage();
        }

        if (errorMessage == null) {
            errorMessage = ex.getClass().getSimpleName();
        }

        return errorMessage;
    }

    private void sendAuthUserEvent(String status, String errorCause, UUID clientCode, String routingKey) {
        AuthUserStatusEventDTO event = new AuthUserStatusEventDTO(status, errorCause, clientCode);

        rabbitTemplate.convertAndSend("auth.user.exchange", routingKey, event);
    }

    private void processEvent (AuthUserEventDTO eventDTO, String outboxCodeStr, String routingKey, Runnable eventAction) {
        UUID outboxCode = UUID.fromString(outboxCodeStr);

        if (isEventProcessed(outboxCode)) return;

        try {
            eventAction.run();
            sendAuthUserEvent("SUCCESS", null, eventDTO.clientCode(), routingKey);
        } catch (Exception ex) {
            log.warn("An unexpected error occurred while trying process the user in the Authentication Server! Client code: '{}'", eventDTO.clientCode());

            String errorMessage = handleExceptionMessage(ex);

            sendAuthUserEvent("FAILED", errorMessage, eventDTO.clientCode(), routingKey);
        }
    }
}
