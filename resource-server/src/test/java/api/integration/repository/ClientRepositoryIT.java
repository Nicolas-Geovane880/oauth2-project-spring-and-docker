package api.integration.repository;

import api.EntityGetter;
import api.IntegrationTestConfig;
import api.entity.Client;
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

@ActiveProfiles ("test")
@DataJpaTest
@AutoConfigureTestDatabase (replace = AutoConfigureTestDatabase.Replace.NONE)
class ClientRepositoryIT extends IntegrationTestConfig {

    @Autowired
    private ClientRepository repository;

    @Autowired
    private EntityManager manager;

    private final EntityGetter entityGetter = new EntityGetter();

    @BeforeEach
    void setUp () {
        repository.deleteAll();
    }

    @Test
    void save () {
        Client saved = repository.save(entityGetter.getClient());

        manager.flush();
        manager.clear();

        Optional<Client> optClient = repository.findById(saved.getId());

        Assertions.assertTrue(optClient.isPresent());
        Assertions.assertEquals(saved.getEmail(), optClient.get().getEmail());
        Assertions.assertEquals(saved.getPhone(), optClient.get().getPhone());
    }
}