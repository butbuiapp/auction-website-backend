package edu.miu.waa.onlineauctionapi.exception;

public class RecordNotFoundException extends RuntimeException {

  private static final long serialVersionUID = -6399875486485173105L;

  public RecordNotFoundException(Exception e) {
    super(e);
  }

  public RecordNotFoundException(String message) {
    super(message);
  }
}
