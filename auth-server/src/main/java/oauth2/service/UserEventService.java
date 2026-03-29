package oauth2.service;

import commons.dto.AuthUserEventDTO;
import commons.dto.AuthUserStatusEventDTO;
import commons.enums.EventType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oauth2.entity.ProcessedEvent;
import oauth2.repository.ProcessedEventRepository;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
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

    @Value (value = "${auth_user_created_key}")
    private String authUserCreatedKey;

    @Value (value = "${auth_user_updated_key}")
    private String authUserUpdatedKey;

    @Value (value = "${auth_user_deleted_key}")
    private String authUserDeletedKey;

    @Value (value = "${auth_exchange}")
    private String authExchange;

    @RabbitListener (bindings = @QueueBinding
            (value = @Queue (value = "${auth_created_queue}", durable = "true"),
             exchange = @Exchange (value = "${api_exchange}", type = "topic"),
             key = "${api_user_created_key}"))
    public void registerUserFromEvent (@Payload AuthUserEventDTO eventDTO,
                                       @Header(AmqpHeaders.MESSAGE_ID) String outboxCodeStr) {

        processEvent(eventDTO, outboxCodeStr, authUserCreatedKey, EventType.USER_ACCOUNT_REGISTERED, () -> userService.register(eventDTO));
    }

    @RabbitListener (bindings = @QueueBinding
            (value = @Queue (value = "${auth_updated_queue}", durable = "true"),
             exchange = @Exchange (value = "${api_exchange}", type = "topic"),
             key = "${api_user_updated_key}"))
    public void updateUserFromEvent (@Payload AuthUserEventDTO eventDTO,
                                     @Header(AmqpHeaders.MESSAGE_ID) String outboxCodeStr) {

        processEvent(eventDTO, outboxCodeStr, authUserUpdatedKey, EventType.USER_ACCOUNT_UPDATED, () -> userService.update(eventDTO));
    }

    @RabbitListener (bindings = @QueueBinding
            (value = @Queue (value = "${auth_deleted_queue}", durable = "true"),
             exchange = @Exchange (value = "${api_exchange}", type = "topic"),
             key = "${api_user_deleted_key}"))
    public void deleteUserFromEvent (@Payload AuthUserEventDTO eventDTO,
                                     @Header(AmqpHeaders.MESSAGE_ID) String outboxCodeStr) {

        processEvent(eventDTO, outboxCodeStr, authUserDeletedKey, EventType.USER_ACCOUNT_DELETED, () -> userService.delete(eventDTO.clientCode()));
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

        rabbitTemplate.convertAndSend(authExchange, routingKey, event);
    }

    private void processEvent (AuthUserEventDTO eventDTO, String outboxCodeStr, String routingKey, EventType eventType, Runnable eventAction) {
        UUID outboxCode = UUID.fromString(outboxCodeStr);

        if (isEventProcessed(outboxCode)) return;

        try {
            eventAction.run();
            sendAuthUserEvent("SUCCESS", null, eventDTO.clientCode(), routingKey);
        } catch (Exception ex) {
            log.warn("An unexpected error occurred while trying process the user in the Authentication Server! Client code: '{}'", eventDTO.clientCode());

            String errorMessage = handleExceptionMessage(ex);

            sendAuthUserEvent("FAILED", errorMessage, eventDTO.clientCode(), routingKey);
        } finally {
            processedEventRepository.save(new ProcessedEvent(eventType, outboxCode));
        }
    }
}
