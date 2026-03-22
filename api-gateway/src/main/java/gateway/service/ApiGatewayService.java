package gateway.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gateway.dto.*;
import gateway.dto.event.AuthUserRollbackEvent;
import gateway.dto.event.EventType;
import gateway.dto.event.EventWrapper;
import gateway.exception.ConflictFieldException;
import gateway.exception.FatalErrorException;
import gateway.mapper.AccountRegisterMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ApiGatewayService {

    private final RestClient apiClient;

    private final RestClient authClient;

    private final AccountRegisterMapper mapper;

    private final RabbitTemplate rabbitTemplate;

    public ApiGatewayService (@Value("${app.services.api-server}") String apiClient,
                              @Value("${app.services.auth-server}") String authClient,
                              AccountRegisterMapper mapper,
                              RabbitTemplate rabbitTemplate) {

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        this.apiClient = RestClient.builder().baseUrl(apiClient).requestFactory(factory).build();
        this.authClient = RestClient.builder().baseUrl(authClient).requestFactory(factory).build();
        this.mapper = mapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void orchestrateAccountRegister (AccountRegisterDTO registerDTO) {
        handleValidations(registerDTO);

        AuthResponseDTO authResponse = handlePostAuthRequest(registerDTO);

        try {
            handlePostApiRequest(registerDTO, authResponse.id());
        } catch (Exception ex) {
            log.error("An error occurred while trying create the register in the API ! Triggering rollback.", ex);
            sendRollBackEvent(authResponse.id());
            throw new FatalErrorException("An unexpected error occurred while trying register the user");
        }
    }

    private void handlePostApiRequest (AccountRegisterDTO registerDTO, Long authUserId) {
        ApiRequestDTO requestDTO = mapper.toApiRequestDTO(registerDTO, authUserId);

        apiClient.post()
                .uri("/api/v1/client/")
                .body(requestDTO)
                .retrieve()
                .toBodilessEntity();
    }

    private AuthResponseDTO handlePostAuthRequest (AccountRegisterDTO registerDTO) {
        return authClient.post()
                .uri("/api/v1/auth/")
                .body(mapper.toAuthRequestDTO(registerDTO))
                .retrieve()
                .onStatus(status -> status == HttpStatus.CONFLICT, ((request, response) -> {

                    Map<String, String> errors = new HashMap<>();

                    errors.put("cpf", "Given cpf is already in use");

                    throw new ConflictFieldException(errors, "Some fields are conflicted");
                }))

                .onStatus(status -> status == HttpStatus.INTERNAL_SERVER_ERROR, ((request, response) -> {
                    throw new FatalErrorException("An unexpected error occurred while trying register the user");
                }))

                .body(AuthResponseDTO.class);
    }

    private void handleValidations (AccountRegisterDTO registerDTO) {
        ProfileValidationDTO validationDTO = mapper.toProfileValidationDTO(registerDTO);

        apiClient
            .post()
            .uri("/api/v1/validate/")
            .body(validationDTO)
            .retrieve()
            .onStatus(status -> status == HttpStatus.CONFLICT, (request, response) -> {

                ObjectMapper objectMapper = new ObjectMapper();

                List<String> validationConflict = objectMapper.readValue(response.getBody(), new TypeReference<>(){});

                Map<String, String> errors = new HashMap<>();

                if (validationConflict.contains("phone")) {
                    errors.put("phone", "given phone is already in use");
                }
                if (validationConflict.contains("email")) {
                    errors.put("email", "given email is already in use");
                }

                throw new ConflictFieldException(errors, "Some fields are conflicted");

            }).toBodilessEntity();
    }

    private void sendRollBackEvent (Long authUserId) {
        AuthUserRollbackEvent event = new AuthUserRollbackEvent(authUserId);

        EventWrapper<AuthUserRollbackEvent> wrapper = new EventWrapper<>
                (EventType.AUTH_USER_ROLLBACK, "v1", event);

        rabbitTemplate.convertAndSend("rollback.exchange", "api.failed", wrapper);
    }
}
