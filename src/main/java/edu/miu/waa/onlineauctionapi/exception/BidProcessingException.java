package edu.miu.waa.onlineauctionapi.exception;

import java.io.Serial;

public class BidProcessingException extends Exception {

  @Serial private static final long serialVersionUID = 2037193625869501114L;

  public BidProcessingException(Exception e) {
    super(e);
  }

  public BidProcessingException(final String message) {
    super(message);
  }

  public BidProcessingException(final String message, Exception e) {
    super(message);
  }
}
