package api.service;

import api.entity.Client;
import api.exception.NoResourceFoundException;
import api.repository.ClientRepository;
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
                .orElseThrow(() -> new NoResourceFoundException("Client not found", "code", code.toString()));
    }

    public boolean existsByEmail (String email) {
        return repository.existsByEmail(email);
    }

    public boolean existsByPhone (String phone) {
        return repository.existsByPhone(phone);
    }

    public boolean existsByCode (UUID code) {
        return repository.existsByCode(code);
    }

    public void deleteByCode (UUID code) {
        repository.deleteByCode(code);
    }

}
