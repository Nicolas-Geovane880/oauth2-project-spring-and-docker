package gateway.exception;

import java.util.Map;

public class ConflictFieldException extends RuntimeException {

    public final Map<String, String> errorsConflict;

    public ConflictFieldException(Map<String, String> errorsConflict, String message) {
        super(message);
        this.errorsConflict = errorsConflict;
    }
}
