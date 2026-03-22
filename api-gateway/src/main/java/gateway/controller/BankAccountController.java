package gateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping (value = "/gateway/v1/bank-account")
public class BankAccountController {

    private final RestClient restClient;

    public BankAccountController (@Value ("${app.services.api-server}") String restClient) {

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        this.restClient = RestClient.builder().baseUrl(restClient).requestFactory(factory).build();
    }

    @GetMapping (value = "/extract")
    public ResponseEntity<String> extract (@RequestHeader HttpHeaders headers) {

        ResponseEntity<String> response = restClient.get()
                .uri("/api/v1/client/extract")
                .headers(h -> h.addAll(headers))
                .retrieve()
                .toEntity(String.class);

        return new ResponseEntity<>(response.getBody(), response.getHeaders(), response.getStatusCode());
    }
}
