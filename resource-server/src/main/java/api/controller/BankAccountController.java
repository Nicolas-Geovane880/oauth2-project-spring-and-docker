package api.controller;

import api.dto.ApiRequestDTO;
import api.dto.ExtractResponseDTO;
import api.dto.TransferResponseDTO;
import api.service.AccountService;
import api.service.ClientAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping (value = "/api/v1/client")
@RequiredArgsConstructor
public class BankAccountController {

    private final AccountService service;

    private final ClientAccountService clientAccountService;

    @PostMapping ("/")
    public ResponseEntity<Void> register (@RequestBody ApiRequestDTO requestDTO) {
        service.saveClientAndAccount(requestDTO);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/extract")
    public ResponseEntity<ExtractResponseDTO> extract (@AuthenticationPrincipal Jwt principal) {
        long authUserId = Long.parseLong(principal.getSubject());

        ExtractResponseDTO response = clientAccountService.extract(authUserId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
