package oauth2.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import oauth2.dto.events_dto.AuthUserRollbackEvent;
import oauth2.dto.events_dto.EventType;
import oauth2.exception.ConflictFieldException;
import oauth2.dto.AuthRequestDTO;
import oauth2.dto.events_dto.EventWrapper;
import oauth2.dto.UserResponseDTO;
import oauth2.entity.Role;
import oauth2.entity.User;
import oauth2.entity.UserStatus;
import oauth2.mapper.UserMapper;
import oauth2.repository.RoleRepository;
import oauth2.repository.UserRepository;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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

        User saved = repository.save(user);

        return mapper.parseToResponse(saved);
    }

    @Transactional
    @RabbitListener (bindings = @QueueBinding
            (value = @Queue (value = "auth.server.rollback.event", durable = "true"),
            exchange = @Exchange (value = "rollback.exchange", type = "topic"),
            key = "api.failed"))
    public void listenRollBackEvent (EventWrapper<?> eventWrapper) {

        if (eventWrapper.getEventType() == EventType.AUTH_USER_ROLLBACK) {
            ObjectMapper objectMapper = new ObjectMapper();

            AuthUserRollbackEvent payload = objectMapper.convertValue
                    (eventWrapper.getPayload(), AuthUserRollbackEvent.class);

            if (repository.existsById(payload.authUserId())) {
                repository.deleteById(payload.authUserId());
            }
        }
    }
}
