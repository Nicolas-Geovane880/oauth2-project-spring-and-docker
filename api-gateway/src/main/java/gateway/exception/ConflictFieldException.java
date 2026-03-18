package gateway.exception;

import java.util.Map;

public class ConflictFieldException extends RuntimeException {

    public final Map<String, String> errorsConflict;

    public ConflictFieldException(Map<String, String> errorsConflict) {
        this.errorsConflict = errorsConflict;
    }
}
