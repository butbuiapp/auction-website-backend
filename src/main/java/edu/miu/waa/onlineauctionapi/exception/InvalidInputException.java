package edu.miu.waa.onlineauctionapi.exception;

public class InvalidInputException extends RuntimeException {

  private static final long serialVersionUID = -7572787248653397517L;

  public InvalidInputException(Exception e) {
    super(e);
  }

  public InvalidInputException(final String message) {
    super(message);
  }
}
