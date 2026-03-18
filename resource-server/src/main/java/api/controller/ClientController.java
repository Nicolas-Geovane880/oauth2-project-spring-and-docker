package api.controller;

import api.dto.ApiRequestDTO;
import api.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping (value = "/api/v1/client")
@RequiredArgsConstructor
public class ClientController {

    private final AccountService service;

    @PostMapping ("/")
    public ResponseEntity<Void> register (@RequestBody ApiRequestDTO requestDTO) {
        service.saveClientAndAccount(requestDTO);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
