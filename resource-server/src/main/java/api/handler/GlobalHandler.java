package api.handler;

import api.exception.InvalidTransferException;
import api.exception.NoResourceFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalHandler {

    private final MessageSource messageSource;

    @ExceptionHandler (InvalidTransferException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidTransferException (InvalidTransferException ex,
                                                                             WebRequest request) {
        String translatedMessage = translateMessage(ex.getMessage());

        ExceptionResponse details = ExceptionResponse.createDetails(translatedMessage, request, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler (NoResourceFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNoResourceFoundException (NoResourceFoundException ex,
                                                                             WebRequest request) {
        String translatedMessage = translateMessage(ex.getMessage());

        ExceptionResponse details = ExceptionResponse.createDetails(translatedMessage, request, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler (Exception.class)
    public ResponseEntity<ExceptionResponse> handleException (Exception ex,
                                                              WebRequest request) {
        String translatedMessage = translateMessage(ex.getMessage());

        ExceptionResponse details = ExceptionResponse.createDetails(translatedMessage, request, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(details, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String translateMessage (String message) {
        return messageSource.getMessage(message, null, message, LocaleContextHolder.getLocale());
    }
}
