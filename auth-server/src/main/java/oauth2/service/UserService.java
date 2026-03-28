package oauth2.service;

import lombok.RequiredArgsConstructor;
import oauth2.dto.events_dto.AuthUserEventDTO;
import oauth2.exception.ConflictFieldException;
import oauth2.dto.UserResponseDTO;
import oauth2.entity.Role;
import oauth2.entity.User;
import oauth2.entity.UserStatus;
import oauth2.mapper.UserMapper;
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

    private final UserMapper mapper;

    @Transactional
    public UserResponseDTO register (AuthUserEventDTO eventDTO) {
        String cpf = eventDTO.cpf();
        String rawPassword = eventDTO.password();

        if (repository.existsByCpf(cpf)) throw new ConflictFieldException();

        Role role = roleRepository.findByRole("ROLE_USER")
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        User user = new User(eventDTO.clientCode(), cpf, passwordEncoder.encode(rawPassword), new UserStatus());
        user.getRoles().add(role);

        User saved = repository.save(user);

        return mapper.parseToResponse(saved);
    }

    @Transactional
    public void update (AuthUserEventDTO eventDTO) {
        User user = repository.findByCode(eventDTO.clientCode())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (eventDTO.cpf() != null) user.setCpf(eventDTO.cpf());

        if (eventDTO.password() != null && !passwordEncoder.matches(eventDTO.password(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(eventDTO.password()));
        }

        repository.saveAndFlush(user);
    }

    @Transactional
    public void delete (UUID clientCode) {
        User user = repository.findByCode(clientCode)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        repository.delete(user);
    }
}
