package gateway.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ExceptionResponse {

    private int statusCode;

    private String message;

    private LocalDateTime timestamp;

    private String path;

    private Map<String, String> errors;

    public static ExceptionResponse createDetails (String message,
                                                   WebRequest request,
                                                   HttpStatus status) {

        String path = request.getDescription(false).replace("uri=", "");

        return ExceptionResponse.builder()
                .statusCode(status.value())
                .message(message)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }

    public static ExceptionResponse createDetails (String message,
                                                   WebRequest request,
                                                   HttpStatus status,
                                                   Map<String, String> errors) {

        String path = request.getDescription(false).replace("uri=", "");

        return ExceptionResponse.builder()
                .statusCode(status.value())
                .message(message)
                .timestamp(LocalDateTime.now())
                .path(path)
                .errors(errors)
                .build();

    }
}
