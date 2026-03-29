package oauth2.service;

import commons.dto.AuthUserEventDTO;
import commons.exception.ConflictFieldsException;
import commons.exception.NoResourceFoundException;
import lombok.RequiredArgsConstructor;
import oauth2.constant.ErrorsMessage;
import oauth2.entity.Role;
import oauth2.entity.User;
import oauth2.entity.UserStatus;
import oauth2.repository.RoleRepository;
import oauth2.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;


    @Transactional
    public void register (AuthUserEventDTO eventDTO) {
        String cpf = eventDTO.cpf();
        String rawPassword = eventDTO.password();

        if (repository.existsByCpf(cpf)) throw new ConflictFieldsException(ErrorsMessage.CONFLICTED_FIELD, null);

        Role role = roleRepository.findByRole("ROLE_USER")
                .orElseThrow(() -> new NoResourceFoundException(ErrorsMessage.ROLE_NOT_FOUND));

        User user = new User(eventDTO.clientCode(), cpf, passwordEncoder.encode(rawPassword), new UserStatus());
        user.getRoles().add(role);

        repository.save(user);
    }

    @Transactional
    public void update (AuthUserEventDTO eventDTO) {
        User user = repository.findByCode(eventDTO.clientCode())
                .orElseThrow(() -> new NoResourceFoundException(ErrorsMessage.USER_NOT_FOUND));

        if (eventDTO.cpf() != null) user.setCpf(eventDTO.cpf());

        if (eventDTO.password() != null && !passwordEncoder.matches(eventDTO.password(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(eventDTO.password()));
        }

        repository.saveAndFlush(user);
    }

    @Transactional
    public void delete (UUID clientCode) {
        User user = repository.findByCode(clientCode)
                .orElseThrow(() -> new NoResourceFoundException(ErrorsMessage.USER_NOT_FOUND));

        repository.delete(user);
    }
}
