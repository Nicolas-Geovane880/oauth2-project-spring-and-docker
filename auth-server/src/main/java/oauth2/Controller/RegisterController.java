package oauth2.Controller;

import lombok.RequiredArgsConstructor;
import oauth2.dto.UserRegisterDTO;
import oauth2.dto.UserResponseDTO;
import oauth2.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping (value = "/auth/v1")
@RequiredArgsConstructor
public class RegisterController {

    private final UserService service;

    @PostMapping (value = "/register")
    public ResponseEntity<UserResponseDTO> register (@RequestBody UserRegisterDTO registerDTO) {
        UserResponseDTO response = service.register(registerDTO);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
