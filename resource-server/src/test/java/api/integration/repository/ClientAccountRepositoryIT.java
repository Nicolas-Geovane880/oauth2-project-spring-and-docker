package api.integration.repository;

import api.EntityGetter;
import api.IntegrationTestConfig;
import api.entity.Client;
import api.entity.ClientAccount;
import api.repository.ClientAccountRepository;
import api.repository.ClientRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.Optional;

@DataJpaTest
@ActiveProfiles ("test")
@AutoConfigureTestDatabase (replace = AutoConfigureTestDatabase.Replace.NONE)
public class ClientAccountRepositoryIT extends IntegrationTestConfig {

    @Autowired
    private ClientAccountRepository repository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private EntityManager manager;

    private final EntityGetter entityGetter = new EntityGetter();

    @BeforeEach
    void setUp () {
        repository.deleteAll();
    }

    @Test
    void save () {
        Client savedClient = clientRepository.save(entityGetter.getClient());

        ClientAccount clientAccount = entityGetter.getClientAccount(savedClient);

        ClientAccount saved = repository.save(clientAccount);

        manager.flush();
        manager.clear();

        Optional<ClientAccount> optClientAccount = repository.findById(saved.getId());

        Assertions.assertTrue(optClientAccount.isPresent());
        Assertions.assertEquals(saved.getClient().getId(), optClientAccount.get().getClient().getId());
        Assertions.assertEquals(saved.getCpf(), optClientAccount.get().getCpf());
    }
}

