package commons.exception;

import java.util.Map;

public class ConflictFieldsException extends RuntimeException {

    public Map<String, String> conflicts;

    public ConflictFieldsException(String message, Map<String, String> conflicts) {
        super(message);
        this.conflicts = conflicts;
    }
}
