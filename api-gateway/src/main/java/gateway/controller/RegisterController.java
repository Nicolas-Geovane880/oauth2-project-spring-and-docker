package gateway.controller;

import gateway.dto.AccountRegisterDTO;
import gateway.service.ApiGatewayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping (value = "/gateway/v1/account")
@RequiredArgsConstructor
public class RegisterController {

    private final ApiGatewayService service;

    @PostMapping (value = "/")
    public ResponseEntity<Void> register (@Valid @RequestBody AccountRegisterDTO registerDTO) {
        service.orchestrateAccountRegister(registerDTO);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}

