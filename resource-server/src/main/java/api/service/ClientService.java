package api.service;

import api.dto.event_dto.UserCreatedEvent;
import api.entity.Client;
import api.factory.ClientFactory;
import api.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository repository;

    private final ClientFactory factory = new ClientFactory();

    public Client createClientFromEvent (UserCreatedEvent event) {
        Client client = factory.newClientFromEvent(event);

        return repository.save(client);
    }

    public boolean existsByEmail (String email) {
        return repository.existsByEmail(email);
    }

}
