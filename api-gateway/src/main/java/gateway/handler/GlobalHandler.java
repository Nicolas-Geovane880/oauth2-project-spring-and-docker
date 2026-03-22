package gateway.handler;

import gateway.constant.ErrorsMessage;
import gateway.exception.ConflictFieldException;
import gateway.exception.FatalErrorException;
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

    @ExceptionHandler (ConflictFieldException.class)
    public ResponseEntity<ExceptionResponse> handleConflictFieldException (ConflictFieldException ex,
                                                                           WebRequest request) {
        String translatedMessage = translateMessage(ex.getMessage());

        ExceptionResponse details = ExceptionResponse.createDetails(translatedMessage, request, HttpStatus.CONFLICT, ex.errorsConflict);

        return new ResponseEntity<>(details, HttpStatus.CONFLICT);
    }

    @ExceptionHandler (FatalErrorException.class)
    public ResponseEntity<ExceptionResponse> handleFatalErrorException (FatalErrorException ex,
                                                                        WebRequest request) {
        String translatedMessage = translateMessage(ex.getMessage());

        ExceptionResponse details = ExceptionResponse.createDetails(translatedMessage, request, HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(details, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler (MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException (MethodArgumentNotValidException ex,
                                                                                    WebRequest request) {
        Map<String, String> errors = ex.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField,
                        field -> field.getDefaultMessage() == null ? "Invalid field" : field.getDefaultMessage()));

        String translatedMessage = translateMessage(ErrorsMessage.INVALID_FIELD);

        ExceptionResponse details = ExceptionResponse.createDetails(translatedMessage, request, HttpStatus.BAD_REQUEST, errors);

        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler (Exception.class)
    public ResponseEntity<ExceptionResponse> handleException (Exception ex,
                                                              WebRequest request) {
        String translatedMessage = translateMessage(ex.getMessage());

        ExceptionResponse details = ExceptionResponse.createDetails(translatedMessage, request, HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(details, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String translateMessage (String message) {
        try {
            // Tenta buscar sem mensagem default
            return messageSource.getMessage(message, null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return "⚠️ TRADUÇÃO NÃO ENCONTRADA PARA: " + message;
        }
    }
}
