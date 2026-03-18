package gateway.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gateway.dto.*;
import gateway.exception.ConflictFieldException;
import gateway.mapper.AccountRegisterMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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

    public ApiGatewayService (@Value("${app.services.api-server}") String apiClient,
                              @Value("${app.services.auth-server}") String authClient,
                              AccountRegisterMapper mapper) {

        this.apiClient = RestClient.builder().baseUrl(apiClient).build();
        this.authClient = RestClient.builder().baseUrl(authClient).build();
        this.mapper = mapper;
    }

    public void orchestrateAccountRegister (AccountRegisterDTO registerDTO) {
        handleValidations(registerDTO);

        AuthResponseDTO authResponse = handlePostAuthRequest(registerDTO);

        try {
            handlePostApiRequest(registerDTO, authResponse.id());
        } catch (Exception ex) {
            log.error("An error occurred while trying create the register in the API ! Triggering rollback.", ex);
            handleRollBack(authResponse.id());
            throw new RuntimeException("An unexpected error occurred while register the user");
        }
    }

    private void handlePostApiRequest (AccountRegisterDTO registerDTO, Long authUserId) {
        System.out.println("API POST");

        ApiRequestDTO requestDTO = mapper.toApiRequestDTO(registerDTO, authUserId);

        apiClient.post()
                .uri("/api/v1/client/")
                .body(requestDTO)
                .retrieve()
                .toBodilessEntity();
    }

    private AuthResponseDTO handlePostAuthRequest (AccountRegisterDTO registerDTO) {
        System.out.println("AUTH POST");


        return authClient.post()
                .uri("/api/v1/auth/")
                .body(mapper.toAuthRequestDTO(registerDTO))
                .retrieve()
                .onStatus(status -> status == HttpStatus.CONFLICT, ((request, response) -> {

                    throw new IllegalArgumentException("Given cpf is already in use");
                }))
                .onStatus(status -> status == HttpStatus.INTERNAL_SERVER_ERROR, ((request, response) -> {
                    throw new RuntimeException("An unexpected error occurred");
                }))

                .body(AuthResponseDTO.class);
    }

    private void handleValidations (AccountRegisterDTO registerDTO) {
        System.out.println("VALIDATION POST");

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

                throw new ConflictFieldException(errors);

            }).toBodilessEntity();
    }

    private void handleRollBack (Long authUserId) {
        System.out.println("ROLLBACK");

        try {
            authClient.delete()
                    .uri("/api/v1/user/" + authUserId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ex) {
            log.error("Fatal error occurred while trying rollback with auth user id {} ! ", authUserId, ex);
        }
    }
}
