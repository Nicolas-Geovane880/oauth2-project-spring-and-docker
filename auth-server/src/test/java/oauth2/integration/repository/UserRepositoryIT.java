package oauth2.integration.repository;

import jakarta.persistence.EntityManager;
import oauth2.EntityGetter;
import oauth2.IntegrationTestConfig;
import oauth2.entity.User;
import oauth2.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase (replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryIT extends IntegrationTestConfig {

    @Autowired
    private UserRepository repository;

    @TestConfiguration
    static class EncoderPasswordConfig {

        @Bean
        public PasswordEncoder passwordEncoder () {
            return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        }
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager manager;

    private final EntityGetter entityGetter = new EntityGetter();

    @BeforeEach
    void setUp () {
        repository.deleteAll();
    }

    @Test
    void save () {
        User user = entityGetter.getUser();
        String rawPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(rawPassword));

        User saved = repository.save(user);

        manager.flush();
        manager.clear();

        Optional<User> optUser = repository.findById(saved.getId());

        Assertions.assertTrue(optUser.isPresent());
        Assertions.assertTrue(passwordEncoder.matches(rawPassword, optUser.get().getPassword()));
        Assertions.assertEquals(user.getCpf(), optUser.get().getCpf());
    }

    @Test
    void findByCpf () {
        User user = entityGetter.getUser();

        User saved = repository.save(user);

        Optional<User> optUser = repository.findByCpf(saved.getCpf());

        Assertions.assertTrue(optUser.isPresent());
        Assertions.assertEquals(saved.getCpf(), optUser.get().getCpf());
        Assertions.assertEquals(saved.getId(), optUser.get().getId());
    }
}
