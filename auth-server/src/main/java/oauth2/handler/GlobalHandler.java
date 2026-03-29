package oauth2.handler;

import commons.exception.ConflictFieldsException;
import commons.exception.NoResourceFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalHandler {

    private final MessageSource messageSource;

    @ExceptionHandler (ConflictFieldsException.class)
    public ResponseEntity<String> handleConflictFieldException (ConflictFieldsException ex) {

        String translatedMessage = translateMessage(ex.getMessage());

        return new ResponseEntity<>(translatedMessage, HttpStatus.CONFLICT);
    }

    @ExceptionHandler (NoResourceFoundException.class)
    public ResponseEntity<String> handleNoResourceFoundException (NoResourceFoundException ex) {

        String translatedMessage = translateMessage(ex.getMessage());

        return new ResponseEntity<>(translatedMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler (Exception.class)
    public ResponseEntity<String> handleException (Exception ex) {

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String translateMessage (String message) {
        return messageSource.getMessage(message, null, message, LocaleContextHolder.getLocale());
    }
}
