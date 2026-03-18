package api.service;

import api.dto.ApiRequestDTO;
import api.dto.event_dto.UserCreatedEvent;
import api.entity.Client;
import api.factory.ClientFactory;
import api.mapper.AccountRegisterMapper;
import api.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository repository;

    public Client save (Client client) {
        return repository.save(client);
    }

}
