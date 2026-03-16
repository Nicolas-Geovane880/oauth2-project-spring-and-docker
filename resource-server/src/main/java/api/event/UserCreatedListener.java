package api.event;

import api.dto.event_dto.EventWrapper;
import api.dto.event_dto.UserCreatedEvent;
import api.entity.AccountTransferLock;
import api.entity.Client;
import api.entity.ClientAccount;
import api.enums.EventType;
import api.repository.ClientAccountRepository;
import api.repository.ClientRepository;
import api.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserCreatedListener {

    private final ProcessedEventRepository processedEventRepository;

    @Transactional
    @RabbitListener (bindings = @QueueBinding (
            value = @Queue (value = "api.client.created.event", durable = "true"),
            exchange = @Exchange (value = "auth.exchange", type = "topic"),
            key = "user.created"
    ))
    public void userCreatedEventListener (EventWrapper<?> eventWrapper) {
        String eventUuid = eventWrapper.getUuid().toString();

        if (processedEventRepository.existsByUuid(eventUuid)) {
            return;
        }




//        Client client = Client.builder()
//                .name(event.name())
//                .lastName(event.lastName())
//                .email(event.email())
//                .birthDate(event.birthDate())
//                .phone(event.phone())
//                .age(LocalDate.now().getYear() - event.birthDate().getYear())
//                .authUserId(event.authUserId())
//                .build();
//
//        ClientAccount clientAccount = ClientAccount.builder()
//                .client(client)
//                .cpf(event.cpf())
//                .accountTransferLock(new AccountTransferLock())
//                .balance(5000.0)
//                .build();
//
//        Client savedClient = clientRepository.save(client);
//
//        ClientAccount savedAccount = clientAccountRepository.save(clientAccount);
    }


}
