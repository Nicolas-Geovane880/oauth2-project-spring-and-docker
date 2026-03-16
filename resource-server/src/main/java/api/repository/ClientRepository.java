package api.repository;

import api.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByEmail (String email);

    boolean existsByPhone (String phone);
}
