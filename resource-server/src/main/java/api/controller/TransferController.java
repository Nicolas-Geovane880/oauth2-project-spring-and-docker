package api.controller;

import api.dto.TransferCreateDTO;
import api.dto.TransferResponseDTO;
import api.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping ("/api/v1/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService service;

    @PostMapping (value = "/")
    public ResponseEntity<TransferResponseDTO> makeTransfer (@AuthenticationPrincipal Jwt principal, @RequestBody TransferCreateDTO requestDTO) {
        UUID clientCode = UUID.fromString(principal.getSubject());

        TransferResponseDTO response = service.makeTransfer(clientCode, requestDTO);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping (value = "/transfers")
    public ResponseEntity<Page<TransferResponseDTO>> transferHistoric (@AuthenticationPrincipal Jwt principal,
                                                                       @RequestParam (required = false, defaultValue = "0") int page,
                                                                       @RequestParam (required = false, defaultValue = "10") int size) {
        UUID clientCode = UUID.fromString(principal.getSubject());

        Page<TransferResponseDTO> response = service.transferHistoric(clientCode, page, size);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
