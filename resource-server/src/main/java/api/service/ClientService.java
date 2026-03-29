package api.service;

import api.constant.ErrorsMessage;
import api.entity.Client;
import api.enums.Status;
import api.repository.ClientRepository;
import commons.exception.NoResourceFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository repository;

    public Client save (Client client) {
        return repository.save(client);
    }

    public Client findByCode (UUID code) {
        return repository.findByCode(code)
                .orElseThrow(() -> new NoResourceFoundException(ErrorsMessage.CLIENT_NOT_FOUND));
    }

    public void softDeleteClient (UUID code) {
        Client client = findByCode(code);

        client.setName("deleted");
        client.setLastName("deleted");
        client.setBirthDate(null);
        client.setPhone("deleted.phone." + code);
        client.setEmail("deleted.email." + code);
        client.setAge(0);
        client.setStatus(Status.DELETED);

        repository.save(client);
    }

    public boolean existsByEmail (String email) {
        return repository.existsByEmail(email);
    }

    public boolean existsByPhone (String phone) {
        return repository.existsByPhone(phone);
    }

    public void delete (Client client) { repository.delete(client); }
}
