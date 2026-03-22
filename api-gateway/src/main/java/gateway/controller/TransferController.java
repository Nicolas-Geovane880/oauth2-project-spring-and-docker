package gateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping (value = "/gateway/v1/transfer")
public class TransferController {

    private final RestClient restClient;

    public TransferController (@Value("${app.services.api-server}") String restClient) {

        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);

        this.restClient = RestClient.builder().baseUrl(restClient).requestFactory(factory).build();
    }

    @PostMapping (value = "/")
    public ResponseEntity<String> makeTransfer (@RequestBody String body, @RequestHeader HttpHeaders headers) {

       return restClient.post()
                .uri("/api/v1/transfer/")
                .body(body)
                .headers(h -> h.addAll(headers))
                .exchange(((clientRequest, clientResponse) -> {
                    byte[] bytes = clientResponse.getBody().readAllBytes();
                    String responseBody = new String(bytes, StandardCharsets.UTF_8);

                    return ResponseEntity.status(clientResponse.getStatusCode())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(responseBody);
                }));
    }

    @GetMapping (value = "/transfers")
    public ResponseEntity<String> transfersHistoric (@RequestHeader HttpHeaders headers) {
        ResponseEntity<String> response = restClient.get()
                .uri("/api/v1/transfer/transfers")
                .headers(h -> h.addAll(headers))
                .retrieve()
                .toEntity(String.class);

        return new ResponseEntity<>(response.getBody(), response.getHeaders(), response.getStatusCode());
    }
}
