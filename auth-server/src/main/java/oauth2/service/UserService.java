package oauth2.service;

import lombok.RequiredArgsConstructor;
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
    public UserResponseDTO register (UserRegisterDTO registerDTO) {
        if (repository.existsByCpf(registerDTO.cpf())) throw new IllegalArgumentException("Given CPF is already in use");

        Role role = roleRepository.findByRole("ROLE_USER")
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        String encodedPassword = passwordEncoder.encode(registerDTO.password());

        User toSave = new User(registerDTO.cpf(), encodedPassword, new UserStatus());
        toSave.getRoles().add(role);

        User saved = repository.save(toSave);

        sendUserCreatedMessage(saved, registerDTO);

        return mapper.parseToResponse(saved);
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
