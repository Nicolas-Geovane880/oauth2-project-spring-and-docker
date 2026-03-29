package api.exception;

import lombok.Getter;

@Getter
public class InvalidTransferException extends RuntimeException {


    public InvalidTransferException(String message) {
        super(message);
    }
}
