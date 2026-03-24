package oauth2.service;

import lombok.RequiredArgsConstructor;
import oauth2.dto.events_dto.AuthUserRegisterEventDTO;
import oauth2.exception.ConflictFieldException;
import oauth2.dto.UserResponseDTO;
import oauth2.entity.Role;
import oauth2.entity.User;
import oauth2.entity.UserStatus;
import oauth2.mapper.UserMapper;
import oauth2.repository.RoleRepository;
import oauth2.repository.UserRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper mapper;

    @Transactional
    public UserResponseDTO register (AuthUserRegisterEventDTO requestDTO) {
        String cpf = requestDTO.cpf();
        String rawPassword = requestDTO.password();

        if (repository.existsByCpf(cpf)) throw new ConflictFieldException();

        Role role = roleRepository.findByRole("ROLE_USER")
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        User user = new User(requestDTO.clientCode(), cpf, passwordEncoder.encode(rawPassword), new UserStatus());
        user.getRoles().add(role);

        User saved = repository.save(user);

        return mapper.parseToResponse(saved);
    }
}
