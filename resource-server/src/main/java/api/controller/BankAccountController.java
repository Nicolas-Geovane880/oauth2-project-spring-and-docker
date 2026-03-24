package api.controller;

import api.dto.ExtractResponseDTO;
import api.dto.UserAccountRegisterDTO;
import api.dto.UserAccountResponseDTO;
import api.service.BankAccountOrchestrator;
import api.service.ClientAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping (value = "/api/v1/account")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountOrchestrator service;

    private final ClientAccountService clientAccountService;

    @PostMapping ("/")
    public ResponseEntity<UserAccountResponseDTO> register (@Valid @RequestBody UserAccountRegisterDTO register) {
        UserAccountResponseDTO response = service.saveClientAndAccount(register);

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping(value = "/extract")
    public ResponseEntity<ExtractResponseDTO> extract (@AuthenticationPrincipal Jwt principal) {
        UUID clientCode = UUID.fromString(principal.getSubject());

        ExtractResponseDTO response = clientAccountService.extract(clientCode);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
