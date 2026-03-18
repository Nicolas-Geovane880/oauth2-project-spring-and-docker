package oauth2.service;

import lombok.RequiredArgsConstructor;
import oauth2.exception.ConflictFieldException;
import oauth2.dto.AuthRequestDTO;
import oauth2.dto.events_dto.EventWrapper;
import oauth2.dto.events_dto.UserCreatedEvent;
import oauth2.dto.UserRegisterDTO;
import oauth2.dto.UserResponseDTO;
import oauth2.entity.Role;
import oauth2.entity.User;
import oauth2.entity.UserStatus;
import oauth2.enums.EventType;
import oauth2.mapper.UserMapper;
import oauth2.repository.RoleRepository;
import oauth2.repository.UserRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final RabbitTemplate rabbitTemplate;

    private final UserMapper mapper;

    @Transactional
    public UserResponseDTO register (AuthRequestDTO requestDTO) {
        String cpf = requestDTO.cpf();
        String rawPassword = requestDTO.password();

        if (repository.existsByCpf(cpf)) throw new ConflictFieldException();

        Role role = roleRepository.findByRole("ROLE_USER")
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        User user = new User(cpf, passwordEncoder.encode(rawPassword), new UserStatus());
        user.getRoles().add(role);

        return mapper.parseToResponse(user);
    }

    @Transactional
    public void deleteById (Long id) {
        repository.deleteById(id);
    }

    private void sendUserCreatedMessage (User createdUser, UserRegisterDTO registerDTO) {
        UserCreatedEvent event = UserCreatedEvent.builder()
                .authUserId(createdUser.getId())
                .name(registerDTO.name())
                .lastName(registerDTO.lastName())
                .cpf(registerDTO.cpf())
                .email(registerDTO.email())
                .phone(registerDTO.phone())
                .birthDate(registerDTO.birthDate())
                .build();

        EventWrapper<UserCreatedEvent> eventWrapper = new EventWrapper<>
                (UUID.randomUUID(), EventType.USER_CREATED_EVENT, "v1", LocalDateTime.now(), event);

        rabbitTemplate.convertAndSend("auth.exchange", "user.created", eventWrapper);
    }
}
