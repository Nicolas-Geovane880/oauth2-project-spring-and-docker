package api.exception;

import lombok.Getter;

@Getter
public class NoResourceFoundException extends RuntimeException {

  private final String type;

  private final String resource;

  public NoResourceFoundException(String message, String type,  String resource) {
    super(message);
    this.type = type;
    this.resource = resource;
  }
}
