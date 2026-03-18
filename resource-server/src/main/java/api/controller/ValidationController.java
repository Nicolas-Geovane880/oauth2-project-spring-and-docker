package api.controller;

import api.dto.ProfileValidationDTO;
import api.service.ValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping (value = "/api/v1/validate")
@RequiredArgsConstructor
public class ValidationController {

    private final ValidateService service;

    @PostMapping (value = "/")
    public ResponseEntity<List<String>> handleValidationsConflict (@RequestBody ProfileValidationDTO validationDTO) {
        List<String> validationConflict = service.handleValidationConflict(validationDTO);

        if (validationConflict.isEmpty()) {
            return ResponseEntity.ok().build();
        }

        return new ResponseEntity<>(validationConflict, HttpStatus.CONFLICT);
    }
}
