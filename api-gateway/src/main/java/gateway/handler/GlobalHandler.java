package gateway.handler;

import gateway.exception.ConflictFieldException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalHandler {

    @ExceptionHandler (ConflictFieldException.class)
    public ResponseEntity<ExceptionResponse> handleConflictFieldException (ConflictFieldException ex,
                                                                           WebRequest request) {

        ExceptionResponse details = ExceptionResponse.createDetails(ex.getMessage(), request, HttpStatus.CONFLICT);

        return new ResponseEntity<>(details, HttpStatus.CONFLICT);
    }
}
