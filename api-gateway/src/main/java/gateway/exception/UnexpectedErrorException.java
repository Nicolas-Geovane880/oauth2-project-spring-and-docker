package gateway.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@AllArgsConstructor
@Getter
public class UnexpectedErrorException extends RuntimeException {

    private String exceptionResponse;

    private HttpStatusCode statusCode;

}
