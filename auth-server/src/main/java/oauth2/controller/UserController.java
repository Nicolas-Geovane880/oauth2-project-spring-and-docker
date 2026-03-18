package oauth2.controller;

import lombok.RequiredArgsConstructor;
import oauth2.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping (value = "api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteById (@PathVariable Long id) {
        service.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
