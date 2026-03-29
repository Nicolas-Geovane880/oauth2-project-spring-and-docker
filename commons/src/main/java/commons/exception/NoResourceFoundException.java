package commons.exception;

import lombok.Getter;

@Getter
public class NoResourceFoundException extends RuntimeException {

  public NoResourceFoundException(String message) {super(message);}
}
