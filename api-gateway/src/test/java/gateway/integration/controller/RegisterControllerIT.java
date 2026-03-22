package gateway.integration.controller;

import gateway.constant.ErrorsMessage;
import gateway.dto.AccountRegisterDTO;
import gateway.handler.ExceptionResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDate;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasKey;

@ActiveProfiles ("test")
@SpringBootTest (webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegisterControllerIT {

    @LocalServerPort
    int port;

    @Autowired
    private MessageSource messageSource;

    @BeforeEach
    void setUp () {
        RestAssured.port = port;
    }

    @Test
    void register () {
        AccountRegisterDTO request = AccountRegisterDTO.builder()
                .name("Valid name")
                .cpf("invalid cpf")
                .phone("invalid phone")
                .password("invalid password invalid password invalid password invalid password")
                .birthDate(LocalDate.now().minusYears(20))
                .email("invalid email")
                .build();

        ExceptionResponse exceptionDetails = given()
                    .contentType(ContentType.JSON)
                    .body(request)
                .when()
                    .post("/api/v1/account/")
                .then()
                       .log().all()
                    .statusCode(400)
                    .body("errors", hasKey("email"))
                    .body("errors", hasKey("cpf"))
                    .body("errors", hasKey("email"))
                    .body("errors", hasKey("password"))
                    .extract()
                    .jsonPath()
                    .getObject("", ExceptionResponse.class);

        String expectedMessage = messageSource.getMessage(ErrorsMessage.INVALID_FIELD, null, ErrorsMessage.INVALID_FIELD, LocaleContextHolder.getLocale());

        Assertions.assertEquals(expectedMessage, exceptionDetails.getMessage());
    }
}