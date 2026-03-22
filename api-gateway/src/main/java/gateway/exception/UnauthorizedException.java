package gateway.exception;

public class UnauthorizedException extends RuntimeException {

    public String body;

    public UnauthorizedException (String body) {
        this.body = body;
    }
}
