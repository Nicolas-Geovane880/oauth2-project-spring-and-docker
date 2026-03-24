package api.exception;

import lombok.Getter;

@Getter
public class InvalidTransferException extends RuntimeException {

    private String additionalMessage;

    public InvalidTransferException(String message) {
        super(message);
    }

    public InvalidTransferException(String message, String additionalMessage) {
        super(message);
        this.additionalMessage = additionalMessage;
    }
}
