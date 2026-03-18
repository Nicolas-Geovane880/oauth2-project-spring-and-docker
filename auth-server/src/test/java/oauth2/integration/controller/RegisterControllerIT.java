package oauth2.integration.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import oauth2.EntityGetter;
import oauth2.IntegrationTestConfig;
import oauth2.dto.UserRegisterDTO;
import oauth2.dto.UserResponseDTO;
import oauth2.mapper.UserMapper;
import oauth2.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import static io.restassured.RestAssured.given;

@ActiveProfiles ("test")
@SpringBootTest (webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase (replace = AutoConfigureTestDatabase.Replace.NONE)
class RegisterControllerIT extends IntegrationTestConfig {

    @LocalServerPort
    int port;

    @Autowired
    private UserRepository repository;

    private final EntityGetter entityGetter = new EntityGetter();

    @BeforeEach
    void setUp () {
        RestAssured.port = port;
        repository.deleteAll();
    }

//    @Test
//    void register() {
//        UserRegisterDTO requestBody = entityGetter.getUserRegisterDTO();
//
//        UserResponseDTO response = given()
//                .contentType(ContentType.JSON)
//                .body(requestBody)
//                .when()
//                .post("/auth/v1/register")
//                .then()
//                .statusCode(201)
//                .extract()
//                .jsonPath()
//                .getObject("", UserResponseDTO.class);
//
//
//        Assertions.assertNotNull(response.id());
//    }
}