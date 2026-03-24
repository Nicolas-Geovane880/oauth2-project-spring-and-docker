package gateway.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@AllArgsConstructor
@Getter
public class ConflictFieldException extends RuntimeException {

    private String exceptionBody;

    private HttpStatusCode statusCode;


}
