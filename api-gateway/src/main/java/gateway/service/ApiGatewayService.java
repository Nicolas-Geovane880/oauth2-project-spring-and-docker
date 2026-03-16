package gateway.service;

import gateway.dto.AccountRegisterDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ApiGatewayService {

    private final RestClient apiClient;

    private final RestClient authClient;

    public ApiGatewayService (@Value("${api.services.api-server}") String apiClient,
                              @Value("${api.services.auth-server}") String authClient) {

        this.apiClient = RestClient.builder().baseUrl(apiClient).build();
        this.authClient = RestClient.builder().baseUrl(authClient).build();
    }

    public void orchestrateAccountRegister (AccountRegisterDTO registerDTO) {


    }
}
