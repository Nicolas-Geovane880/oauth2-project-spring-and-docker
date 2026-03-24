package api.handler;

import api.constant.ErrorsMessage;
import api.exception.ConflictFieldsException;
import api.exception.DeserializationException;
import api.exception.InvalidTransferException;
import api.exception.NoResourceFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalHandler {

    private final MessageSource messageSource;

    @ExceptionHandler (InvalidTransferException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidTransferException (InvalidTransferException ex,
                                                                             WebRequest request) {
        String translatedMessage = translateMessage(ex.getMessage()) + (ex.getAdditionalMessage() != null ? ex.getAdditionalMessage() : "");

        ExceptionResponse details = ExceptionResponse.createDetails(translatedMessage, request, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler (NoResourceFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNoResourceFoundException (NoResourceFoundException ex,
                                                                             WebRequest request) {
        String translatedMessage = translateMessage(ex.getMessage()).formatted(ex.getType(), ex.getResource());

        System.out.println(translatedMessage);

        ExceptionResponse details = ExceptionResponse.createDetails(translatedMessage, request, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler (ConflictFieldsException.class)
    public ResponseEntity<ExceptionResponse> handleConflictFieldsException (ConflictFieldsException ex,
                                                                            WebRequest request) {
        String translatedMessage = translateMessage(ex.getMessage());

        ExceptionResponse details = ExceptionResponse.createDetails(translatedMessage, request, HttpStatus.CONFLICT, ex.conflicts);

        return new ResponseEntity<>(details, HttpStatus.CONFLICT);
    }

    @ExceptionHandler (DeserializationException.class)
    public ResponseEntity<ExceptionResponse> handleDeserializationException (DeserializationException ex,
                                                                            WebRequest request) {
        String translatedMessage = translateMessage(ex.getMessage());

        ExceptionResponse details = ExceptionResponse.createDetails(translatedMessage, request, HttpStatus.CONFLICT);

        return new ResponseEntity<>(details, HttpStatus.CONFLICT);
    }

    @ExceptionHandler (MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException (MethodArgumentNotValidException ex,
                                                                                    WebRequest request) {
        String message = translateMessage(ErrorsMessage.INVALID_FIELD);

        Map<String, String> errors = ex.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, field ->
                        field.getDefaultMessage() == null ? "Invalid field" : field.getDefaultMessage()));

        ExceptionResponse details = ExceptionResponse.createDetails(message, request, HttpStatus.BAD_REQUEST, errors);

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
