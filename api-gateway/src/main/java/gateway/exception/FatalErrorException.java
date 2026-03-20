package gateway.exception;

public class FatalErrorException extends RuntimeException {

    public FatalErrorException(String message) {
        super(message);
    }
}
