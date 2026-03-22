package api.controller;

import api.dto.TransferRequestDTO;
import api.dto.TransferResponseDTO;
import api.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/api/v1/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService service;

    @PostMapping (value = "/")
    public ResponseEntity<TransferResponseDTO> makeTransfer (@AuthenticationPrincipal Jwt principal, @RequestBody TransferRequestDTO requestDTO) {
        long sourceAuthUserId = Long.parseLong(principal.getSubject());

        TransferResponseDTO response = service.makeTransfer(sourceAuthUserId, requestDTO);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping (value = "/transfers")
    public ResponseEntity<Page<TransferResponseDTO>> transferHistoric (@AuthenticationPrincipal Jwt principal,
                                                                       @RequestParam (required = false, defaultValue = "0") int page,
                                                                       @RequestParam (required = false, defaultValue = "10") int size) {
        long sourceAuthUserId = Long.parseLong(principal.getSubject());

        Page<TransferResponseDTO> response = service.transferHistoric(sourceAuthUserId, page, size);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
