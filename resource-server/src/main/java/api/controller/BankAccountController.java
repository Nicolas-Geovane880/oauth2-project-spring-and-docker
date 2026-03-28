package api.controller;

import api.dto.ExtractResponseDTO;
import api.dto.BankAccountRegisterDTO;
import api.dto.UserAccountResponseDTO;
import api.dto.UserAccountUpdateDTO;
import api.service.BankAccountService;
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

    private final BankAccountService service;

    private final ClientAccountService clientAccountService;

    @PostMapping ("/")
    public ResponseEntity<UserAccountResponseDTO> register (@Valid @RequestBody BankAccountRegisterDTO register) {
        UserAccountResponseDTO response = service.registerBankAccount(register);

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping(value = "/extract")
    public ResponseEntity<ExtractResponseDTO> extract (@AuthenticationPrincipal Jwt principal) {
        UUID clientCode = UUID.fromString(principal.getSubject());

        ExtractResponseDTO response = clientAccountService.extract(clientCode);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping (value = "/me")
    public ResponseEntity<UserAccountResponseDTO> me (@AuthenticationPrincipal Jwt principal) {
        UUID clientCode = UUID.fromString(principal.getSubject());

        UserAccountResponseDTO response = service.me(clientCode);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping (value = "/update")
    public ResponseEntity<Void> update (@AuthenticationPrincipal Jwt principal, @Valid @RequestBody UserAccountUpdateDTO updateDTO) {
        UUID clientCode = UUID.fromString(principal.getSubject());

        service.update(clientCode, updateDTO);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @DeleteMapping (value = "/delete")
    public ResponseEntity<Void> delete (@AuthenticationPrincipal Jwt principal) {
        UUID clientCode = UUID.fromString(principal.getSubject());

        service.softDeleteBankAccount(clientCode);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
