package gateway.integration.controller;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
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

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasKey;

@ActiveProfiles ("test")
@SpringBootTest (webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WireMockTest (httpPort = 8090)
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
    void shouldRevokeTheRegister () {
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

    @Test
    void shouldPassTheRegisterSuccessfully () {
        AccountRegisterDTO request = AccountRegisterDTO.builder()
                .name("Valid name")
                .cpf("12345678900")
                .phone("(00) 1234-5678")
                .password("Valid_password")
                .birthDate(LocalDate.now().minusYears(20))
                .email("valid@mail.com")
                .build();

        stubFor(post(urlEqualTo("/api/v1/validate/"))
                .willReturn(aResponse().withStatus(200)));

        stubFor(post(urlEqualTo("/api/v1/auth/"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(201)
                        .withBody("""
                                {
                                    "id": 10,
                                    "createdAt": "2026-03-19T20:00:00Z"
                                }
                                """)));

        stubFor(post(urlEqualTo("/api/v1/client/"))
                .willReturn(aResponse()
                        .withStatus(201)));

        given()
                .contentType(ContentType.JSON)
                .body(request)
            .when()
                .post("/api/v1/account/")
            .then()
                .log().all()
                .statusCode(201);
    }
}