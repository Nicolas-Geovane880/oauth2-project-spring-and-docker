package oauth2.controller;

import lombok.RequiredArgsConstructor;
import oauth2.dto.AuthRequestDTO;
import oauth2.dto.UserResponseDTO;
import oauth2.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping (value = "api/v1/auth")
@RequiredArgsConstructor
public class RegisterController {

    private final UserService service;

    @PostMapping (value = "/")
    public ResponseEntity<UserResponseDTO> register (@RequestBody AuthRequestDTO registerDTO) {
        UserResponseDTO response = service.register(registerDTO);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
