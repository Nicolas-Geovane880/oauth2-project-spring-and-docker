package oauth2.repository;

import oauth2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByCpf (String cpf);

    boolean existsByCpf (String cpf);
}
